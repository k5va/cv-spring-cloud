package org.k5va.mapper;

import org.k5va.events.CreateCvEvent;
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

    public CvDto toCvDto(CreateCvEvent createCvEvent) {
        return new CvDto(null,
                createCvEvent.education(),
                createCvEvent.description(),
                createCvEvent.workExperience(),
                createCvEvent.skills(),
                createCvEvent.languages(),
                createCvEvent.certificates(),
                createCvEvent.linkedId(),
                createCvEvent.isOpenToWork(),
                createCvEvent.employeeId());
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
