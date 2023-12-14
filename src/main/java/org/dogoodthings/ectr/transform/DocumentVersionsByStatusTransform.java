package org.dogoodthings.ectr.transform;

import com.dscsag.plm.spi.interfaces.DocumentKey;
import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.logging.PlmLogger;
import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.rfc.RfcCall;
import com.dscsag.plm.spi.interfaces.rfc.RfcExecutor;
import com.dscsag.plm.spi.interfaces.rfc.RfcResult;
import com.dscsag.plm.spi.interfaces.rfc.RfcTable;
import com.dscsag.plm.spi.interfaces.services.Message;
import com.dscsag.plm.spi.interfaces.services.ServiceResult;
import com.dscsag.plm.spi.interfaces.services.document.key.KeyConverterService;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectConfiguration;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectService;
import com.dscsag.plm.spi.rfc.builder2.FmCallBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DocumentVersionsByStatusTransform implements TransformObjectService {

  private final ServiceTool serviceTool;

  public DocumentVersionsByStatusTransform(ServiceTool serviceTool) {
    this.serviceTool = serviceTool;
  }

  @Override
  public ServiceResult<TransformObjectResult> execute(TransformObjectConfiguration transformObjectConfiguration) {
    List<PlmObjectKey> keys;
    try {
      keys = findVersions(transformObjectConfiguration);
    } catch (Exception e) {
      return ServiceResult.failed(Message.error("ERROR-EXCEPTION", e.getMessage()));
    }
    return ServiceResult.successWithObject(new Result(keys));
  }

  private List<PlmObjectKey> findVersions(TransformObjectConfiguration transformObjectConfiguration) throws Exception {
    KeyConverterService keyConverterService = serviceTool.getService(KeyConverterService.class);
    List<PlmObjectKey> resultKeys = new ArrayList<>();
    String documentStatus = transformObjectConfiguration.parameters().get("STATUS");
    if (documentStatus == null || documentStatus.trim().isEmpty())
      documentStatus = "*";
    //take only documents and convert keys to document keys
    List<DocumentKey> docKeys = transformObjectConfiguration.objectKeys().stream()
        .filter(key -> "DRAW".equals(key.getType()))
        .map(keyConverterService::fromPlmObjectKey).toList();
    for (DocumentKey documentKey : docKeys) {
      resultKeys.addAll(findAllCorrespondingVersions(documentKey, documentStatus));
    }
    return resultKeys;
  }

  private Collection<PlmObjectKey> findAllCorrespondingVersions(DocumentKey documentKey, String documentStatus) throws Exception {
    KeyConverterService keyConverterService = serviceTool.getService(KeyConverterService.class);
    List<PlmObjectKey> keys = new ArrayList<>();
    RfcExecutor rfcExecutor = ectrService().getRfcExecutor();
    FmCallBuilder builder = new FmCallBuilder("/DSCSAG/DOC_VERSION_GET_ALL3");
    RfcCall rfcCall = builder.importing()
        .scalar("GETCLASSIFICATION", "")
        .scalar("IV_GET_CUST_ATTR", "")
        .scalar("READ_DATES", "")
        .scalar("READ_STPO", "")
        .scalar("READ_CHECKEDIN", "")
        .scalar("CONVERT_MATNR", "")

        .structure("DOCUMENTKEY",
            "DOCUMENTTYPE", documentKey.getType(),
            "DOCUMENTNUMBER", documentKey.getNumber(),
            "DOCUMENTVERSION", documentKey.getVersion(),
            "DOCUMENTPART", documentKey.getPart())
        .build();

    RfcResult rfcResult = rfcExecutor.execute(rfcCall);
    RfcTable documentsTable = rfcResult.getTable("OUT_DOCUMENT");
    for (int row = 0; row < documentsTable.getRowCount(); row++) {
      var rowStructure = documentsTable.getRow(row);
      if ("*".equals(documentStatus) || documentStatus.equals(rowStructure.getFieldValue("DOKST"))) {
        var key = keyConverterService.documentKeyFor(rowStructure.getFieldValue("DOKAR"),
            rowStructure.getFieldValue("DOKNR"),
            rowStructure.getFieldValue("DOKVR"),
            rowStructure.getFieldValue("DOKTL")
        );
        keys.add(keyConverterService.fromDocumentKey(key));
      }
    }
    return keys;
  }

  private PlmLogger logger() throws Exception {
    return ectrService().getPlmLogger();
  }

  private ECTRService ectrService() throws Exception {
    return serviceTool.getService(ECTRService.class);
  }

  record Result(List<PlmObjectKey> transformedKeys) implements TransformObjectResult {
  }
}
