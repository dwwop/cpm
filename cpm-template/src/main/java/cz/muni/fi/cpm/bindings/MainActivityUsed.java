package cz.muni.fi.cpm.bindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openprovenance.prov.model.QualifiedName;

public class MainActivityUsed {
    private QualifiedName id;
    @JsonProperty(required = true)
    private QualifiedName bcId;

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public QualifiedName getBcId() {
        return bcId;
    }

    public void setBcId(QualifiedName bcId) {
        this.bcId = bcId;
    }
}
