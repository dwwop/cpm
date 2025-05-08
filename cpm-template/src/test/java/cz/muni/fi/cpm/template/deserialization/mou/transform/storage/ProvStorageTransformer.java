package cz.muni.fi.cpm.template.deserialization.mou.transform.storage;

import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.template.deserialization.pbm.PbmFactory;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.template.json.Bindings;

import java.util.Map;

import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_NS;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_PREFIX;
import static cz.muni.fi.cpm.template.deserialization.mou.constants.NameConstants.*;
import static cz.muni.fi.cpm.template.deserialization.pbm.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.template.deserialization.pbm.PbmNamespaceConstants.PBM_PREFIX;

public class ProvStorageTransformer extends StorageTransformer {
    public ProvStorageTransformer(Patient patient, ProvFactory pF, ICpmProvFactory cPF, PbmFactory pbmF) {
        super(patient, pF, cPF, pbmF);
    }

    @Override
    protected Document createTI(String suffix) {

        Bindings bindings = new Bindings();
        bindings.var = Map.of(
                "main_activity_id", newQDescriptor(BBMRI_PREFIX + ":" + STORAGE + suffix),
                "forward_conn_id", newQDescriptor(BBMRI_PREFIX + ":" + STOR_CON + suffix),
                "sender_id", newQDescriptor(BBMRI_PREFIX + ":UNI"),
                "backward_conn_id", newQDescriptor(BBMRI_PREFIX + ":" + ACQUISITION_CON + suffix),
                "bndl", newQDescriptor(BBMRI_PREFIX + ":" + STORAGE + "Bundle" + suffix),
                "ref_id", newQDescriptor(BBMRI_PREFIX + ":" + ACQUISITION + "Bundle" + suffix)
        );

        bindings.context = Map.of(
                BBMRI_PREFIX, BBMRI_NS, PBM_PREFIX, PBM_NS, DCT_PREFIX, DCT_NS
        );

        return newDocument(pF, bindings, "backbone_tmpl_stor");
    }

}
