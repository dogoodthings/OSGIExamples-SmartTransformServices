package org.dogoodthings.ectr.transform;

import com.dscsag.plm.spi.interfaces.services.ServiceConstants;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;
import java.util.Hashtable;


public class Activator implements BundleActivator {
  public void start(BundleContext context) throws Exception {
    ServiceTool serviceTool = new ServiceTool(context);
    Dictionary<String, Object> props = new Hashtable<>();
    props.put(ServiceConstants.IMPLEMENTATION_ALIAS, "dogoodthings.VersionsByStatus");
    context.registerService(TransformObjectService.class, new DocumentVersionsByStatusTransform(serviceTool), props);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    // empty
  }
}
