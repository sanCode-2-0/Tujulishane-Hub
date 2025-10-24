package com.tujulishanehub.backend.converters;

import com.tujulishanehub.backend.models.ApprovalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ApprovalStatusConverter implements AttributeConverter<ApprovalStatus, String> {

    @Override
    public String convertToDatabaseColumn(ApprovalStatus attribute) {
        if (attribute == null) return null;
        // store as upper-case literal to match DB constraint
        return attribute.name().toUpperCase();
    }

    @Override
    public ApprovalStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return ApprovalStatus.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
