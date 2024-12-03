package org.k5va.mapper;

import org.k5va.model.CvDocument;
import org.k5va.dto.CvDto;
import org.springframework.stereotype.Component;

@Component
public class CvMapper {
    public CvDto toCvDto(CvDocument cvDocument) {
        return new CvDto(
                cvDocument.getId(),
                cvDocument.getEducation(),
                cvDocument.getDescription(),
                cvDocument.getWorkExperience(),
                cvDocument.getSkills(),
                cvDocument.getLanguages(),
                cvDocument.getCertificates(),
                cvDocument.getLinkedId(),
                cvDocument.isOpenToWork(),
                cvDocument.getEmployeeId()
        );
    }

    public CvDocument toCvDocument(CvDto cvDto) {
        return new CvDocument(
                cvDto.id(),
                cvDto.education(),
                cvDto.description(),
                cvDto.workExperience(),
                cvDto.skills(),
                cvDto.languages(),
                cvDto.certificates(),
                cvDto.linkedId(),
                cvDto.isOpenToWork(),
                cvDto.employeeId()
        );
    }
}
