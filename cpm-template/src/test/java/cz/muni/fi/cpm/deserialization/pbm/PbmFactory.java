package cz.muni.fi.cpm.deserialization.pbm;

import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Type;

import static cz.muni.fi.cpm.deserialization.pbm.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.deserialization.pbm.PbmNamespaceConstants.PBM_PREFIX;

public class PbmFactory {
    private final ProvFactory pF;

    public PbmFactory(ProvFactory pF) {
        this.pF = pF;
    }

    public Type newType(PbmType type) {
        return pF.newType(
                pF.newQualifiedName(PBM_NS, type.toString(), PBM_PREFIX),
                pF.getName().PROV_QUALIFIED_NAME);
    }
}
