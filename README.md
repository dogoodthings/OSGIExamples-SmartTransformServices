# OSGIExamples-SmartTransformServices
services to use with smart containers

this example needs s4 1.2.1.0+ + plm api 5.2.10.0+


example configuration in default.txt:

`plm.smart.container.DOC_VER_STATUS_RJ.name = All rejected versions
plm.smart.container.DOC_VER_STATUS_RJ.macro = generic_TransformObjectServiceContainer_macro.txt
plm.smart.container.DOC_VER_STATUS_RJ.params.0 = dogoodthings.VersionsByStatus
plm.smart.container.DOC_VER_STATUS_RJ.params.1 = STATUS
plm.smart.container.DOC_VER_STATUS_RJ.params.2 = RJ

plm.smart.container.DOC_VER_STATUS_SW.name = All in work versions
plm.smart.container.DOC_VER_STATUS_SW.macro = generic_TransformObjectServiceContainer_macro.txt
plm.smart.container.DOC_VER_STATUS_SW.params.0 = dogoodthings.VersionsByStatus
plm.smart.container.DOC_VER_STATUS_SW.params.1 = STATUS
plm.smart.container.DOC_VER_STATUS_SW.params.2 = SW

plm.smart.container.DOC_VER_STATUS_AC.name = All accessible versions
plm.smart.container.DOC_VER_STATUS_AC.macro = generic_TransformObjectServiceContainer_macro.txt
plm.smart.container.DOC_VER_STATUS_AC.params.0 = dogoodthings.VersionsByStatus
plm.smart.container.DOC_VER_STATUS_AC.params.1 = STATUS
plm.smart.container.DOC_VER_STATUS_AC.params.2 = AC


plm.group.container.DOC_VER.children = SMART_CONT(DOC_VER_STATUS_RJ);SMART_CONT(DOC_VER_STATUS_SW);SMART_CONT(DOC_VER_STATUS_AC)
plm.group.container.DOC_VER.icon = tree/container/obr_tree_vers
plm.group.container.DOC_VER.name = Versions overview


plm.om.DOC.containers.UGM = @plm.om.DOC.containers.UGM@;GROUP_CONT(DOC_VER)`


change the values to fit your document types and status network

Keep in mind, this is just a demo for smart transform services, ECTR has other, builtin capabilities to show versions    