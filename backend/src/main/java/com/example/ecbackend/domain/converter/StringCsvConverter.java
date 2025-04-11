package com.example.ecbackend.domain.converter;

import com.example.ecbackend.domain.CsvString;
import org.seasar.doma.DomainConverters;
import org.seasar.doma.jdbc.domain.DomainConverter;

@DomainConverters
public class StringCsvConverter implements DomainConverter<CsvString, String> {
    @Override
    public String fromDomainToValue(CsvString domain) {
        return domain.getValue();
    }

    @Override
    public CsvString fromValueToDomain(String value) {
        return new CsvString(value);
    }
} 