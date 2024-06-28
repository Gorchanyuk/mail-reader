package ru.gorchanyuk.mail.reader.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.gorchanyuk.mail.reader.service.ExtractStringService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExtractStringServiceImpl implements ExtractStringService {

    @Override
    public String getSubject(String str, String reg) {
        if (ObjectUtils.isEmpty(str)) {
            return "(без темы)";
        }
        if (str.matches(reg)) {
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return str;
    }
}