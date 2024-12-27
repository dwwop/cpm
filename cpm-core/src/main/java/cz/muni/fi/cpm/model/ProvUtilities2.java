package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.exception.NoSpecificKind;
import cz.muni.fi.cpm.exception.ValueConflict;
import org.openprovenance.prov.model.*;

import java.util.HashSet;
import java.util.Set;

public class ProvUtilities2 {
    // TODO tests
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String VALUE = "value";

    public static StatementOrBundle.Kind getEffectKind(Relation r) throws NoSpecificKind {
        if (r instanceof Used) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof WasStartedBy) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof WasEndedBy) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof WasGeneratedBy) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasDerivedFrom) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasAssociatedWith) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof WasInvalidatedBy) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasAttributedTo) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof AlternateOf) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof SpecializationOf) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof HadMember) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasInformedBy) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof MentionOf) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasInfluencedBy) {
            throw new NoSpecificKind();
        } else if (r instanceof ActedOnBehalfOf) {
            return StatementOrBundle.Kind.PROV_AGENT;
        } else if (r instanceof DerivedByInsertionFrom) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else {
            System.out.println("Unknown relation " + r);
            throw new UnsupportedOperationException();
        }
    }

    public static StatementOrBundle.Kind getCauseKind(Relation r) throws NoSpecificKind {
        if (r instanceof Used) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasGeneratedBy) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof WasInvalidatedBy) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof WasStartedBy) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasEndedBy) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasDerivedFrom) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasInfluencedBy) {
            throw new NoSpecificKind();
        } else if (r instanceof WasAssociatedWith) {
            return StatementOrBundle.Kind.PROV_AGENT;
        } else if (r instanceof WasAttributedTo) {
            return StatementOrBundle.Kind.PROV_AGENT;
        } else if (r instanceof AlternateOf) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof SpecializationOf) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof HadMember) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof MentionOf) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else if (r instanceof WasInformedBy) {
            return StatementOrBundle.Kind.PROV_ACTIVITY;
        } else if (r instanceof ActedOnBehalfOf) {
            return StatementOrBundle.Kind.PROV_AGENT;
        } else if (r instanceof DerivedByInsertionFrom) {
            return StatementOrBundle.Kind.PROV_ENTITY;
        } else {
            System.out.println("Unknown relation " + r);
            throw new UnsupportedOperationException();
        }
    }


    public static <T extends Element> void mergeAttributes(T existing, T newElement) throws ValueConflict {
        Set<LangString> set = new HashSet<>(newElement.getLabel());
        existing.getLabel().forEach(set::remove);
        existing.getLabel().addAll(set);

        Set<Location> set2 = new HashSet<>(newElement.getLocation());
        existing.getLocation().forEach(set2::remove);
        existing.getLocation().addAll(set2);

        Set<Type> set3 = new HashSet<>(newElement.getType());
        existing.getType().forEach(set3::remove);
        existing.getType().addAll(set3);

        Set<Other> set4 = new HashSet<>(newElement.getOther());
        existing.getOther().forEach(set4::remove);
        existing.getOther().addAll(set4);

        if (existing instanceof Activity existingActivity) {
            Activity newActivity = (Activity) newElement;
            if (existingActivity.getStartTime() != null && newActivity.getStartTime() != null
                    && !existingActivity.getStartTime().equals(newActivity.getStartTime())) {
                throw new ValueConflict(existing.getId().toString(), START_TIME, existingActivity.getStartTime().toString(), newActivity.getStartTime().toString());
            }

            if (existingActivity.getEndTime() != null && newActivity.getEndTime() != null
                    && !existingActivity.getEndTime().equals(newActivity.getEndTime())) {
                throw new ValueConflict(existing.getId().toString(), END_TIME, existingActivity.getEndTime().toString(), newActivity.getEndTime().toString());
            }
        }

        if (existing instanceof Entity existingEntity) {
            Entity newEntity = (Entity) newElement;
            if (existingEntity.getValue() != null && newEntity.getValue() != null
                    && !existingEntity.getValue().equals(newEntity.getValue())) {
                throw new ValueConflict(existing.getId().toString(), VALUE, existingEntity.getValue().toString(), newEntity.getValue().toString());
            }
        }
    }

    public static void mergeAttributes(Influence existing, Influence newElement) {
        Set<LangString> set = new HashSet<>(newElement.getLabel());
        existing.getLabel().forEach(set::remove);
        existing.getLabel().addAll(set);

        if (existing instanceof HasLocation existing2) {
            Set<Location> set2 = new HashSet<>(((HasLocation) newElement).getLocation());
            existing2.getLocation().forEach(set2::remove);
            existing2.getLocation().addAll(set2);
        }

        Set<Type> set3 = new HashSet<Type>(newElement.getType());
        existing.getType().forEach(set3::remove);
        existing.getType().addAll(set3);

        Set<Other> set4 = new HashSet<Other>(newElement.getOther());
        existing.getOther().forEach(set4::remove);
        existing.getOther().addAll(set4);
    }

}
