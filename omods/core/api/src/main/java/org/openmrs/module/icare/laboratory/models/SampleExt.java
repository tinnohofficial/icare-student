package org.openmrs.module.icare.laboratory.models;

import org.hibernate.annotations.DiscriminatorFormula;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.icare.ICareConfig;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "lb_sample")
@DiscriminatorValue("00")
public class SampleExt extends Sample {
	
	@Override
    public Map<String, Object> toMap() throws Exception {
        HashMap<String, Object> sampleObject = (new HashMap<String, Object>());
        sampleObject.put("label", this.getLabel());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        if (this.getDateTime() != null) {
            //			sampleObject.put("created", sdf.format(this.getDateTime()));
            sampleObject.put("created", this.getDateTime());
        } else if (this.getDateCreated() != null) {
            //			sampleObject.put("created", sdf.format(this.getDateCreated()));

            sampleObject.put("created", this.getDateCreated());
        }

        if (this.getDateTime() != null) {
            //			sampleObject.put("created", sdf.format(this.getDateTime()));
            sampleObject.put("dateCreated", this.getDateTime());
        } else if (this.getDateCreated() != null) {
            //			sampleObject.put("created", sdf.format(this.getDateCreated()));

            sampleObject.put("dateCreated", this.getDateCreated());
        }

        if (this.getDateTime() != null) {
            //			sampleObject.put("created", sdf.format(this.getDateTime()));
            sampleObject.put("dateTimeCreated", this.getDateTime());
        } else if (this.getDateCreated() != null) {
            //			sampleObject.put("created", sdf.format(this.getDateCreated()));

            sampleObject.put("dateTimeCreated", this.getDateCreated());
        }

        sampleObject.put("uuid", this.getUuid());

        Map<String, Object> creatorObject = new HashMap<String, Object>();

        if (this.getCreator() != null) {
            creatorObject.put("uuid", this.getCreator().getUuid());
            creatorObject.put("display", this.getCreator().getDisplayString());
        }

        Map<String, Object> locationObject = new HashMap<String, Object>();

        if (this.getLocation() != null) {
            locationObject.put("uuid", this.getLocation().getUuid());
            locationObject.put("display", this.getLocation().getDisplayString());
            List<Map<String, Object>> attributes = new ArrayList<>();
            if (this.getLocation().getAttributes().size() > 0) {
                for(LocationAttribute locationAttribute: this.getLocation().getAttributes()) {
                    Map<String, Object> attribute = new HashMap<>();
                    attribute.put("value", locationAttribute.getValue());
                    Map<String, Object> attributeType =  new HashMap<>();
                    attributeType.put("uuid", locationAttribute.getAttributeType().getUuid());
                    attribute.put("attributeType", attributeType);
                    attributes.add(attribute);
                }
            }
            locationObject.put("attributes", attributes);
        }

        sampleObject.put("location", locationObject);
        sampleObject.put("creator", creatorObject);

        sampleObject.put("voided", this.getVoided());
        HashMap<String, Object> visitObject = new HashMap<String, Object>();
        visitObject.put("uuid", this.getVisit().getUuid());
        visitObject.put("startDateTime", this.getVisit().getStartDatetime());
        visitObject.put("stopDateTime", this.getVisit().getStopDatetime());
        List<Map<String, Object>> visitAttributes = new ArrayList<>();
        if ( this.getVisit().getAttributes().size() > 0) {
            for(VisitAttribute visitAttribute: this.getVisit().getAttributes()) {
                Map<String, Object> attribute = new HashMap<>();
                attribute.put("value", visitAttribute.getValue());
                Map<String, Object> attributeType =  new HashMap<>();
                attributeType.put("uuid", visitAttribute.getAttributeType().getUuid());
                attribute.put("attributeType", attributeType);
                visitAttributes.add(attribute);
            }
        }
        visitObject.put("attributes", visitAttributes);
        sampleObject.put("visit", visitObject);

        //
        //		HashMap<String, Object> patientObject = new HashMap<String, Object>();
        //		patientObject.put("MRN",this.getVisit().getPatient().getPatientIdentifier().getIdentifier());
        //		patientObject.put("name",this.getVisit().getPatient().getPerson().getGivenName().concat((" ").concat(this.getVisit().getPatient().getPerson().getGivenName())));
        //		sampleObject.put("patient", patientObject);

        HashMap<String, Object> conceptObject = new HashMap<String, Object>();
        conceptObject.put("uuid", this.getConcept().getUuid());

        sampleObject.put("concept", conceptObject);

        List<Map<String, Object>> orders = new ArrayList<Map<String, Object>>();

        for (SampleOrder sampleOrder : this.getSampleOrders()) {
                boolean excludeAllocations = true;
            if (!sampleOrder.getOrder().getVoided()) {
                orders.add(sampleOrder.toMap(excludeAllocations));
            }
        }

        sampleObject.put("orders", orders);

        List<Map<String, Object>> sampleStatusesList = new ArrayList<Map<String, Object>>();

        for (SampleStatus sampleStatus : this.getSampleStatuses()) {

            sampleStatusesList.add(sampleStatus.toMap());
        }

        sampleObject.put("statuses", sampleStatusesList);

        //add sample patietient details
        HashMap<String, Object> patientObject = new HashMap<String, Object>();
        patientObject.put("uuid", this.getVisit().getPatient().getUuid());
        patientObject.put("allergy", this.getVisit().getPatient().getAllergyStatus());
        //		patientObject.put("dob", this.getVisit().getPatient().getPerson().getBirthDateTime());
        patientObject.put("dob", this.getVisit().getPatient().getPerson().getBirthdate());

        //		if (this.getVisit().getPatient().getBirthDateTime() == null) {
        //			if (this.getVisit().getPatient().getPerson().getBirthDateTime() == null) {
        //
        //			} else {
        //				patientObject.put("dob", this.getVisit().getPatient().getPerson().getBirthDateTime());
        //			}
        //
        //		} else {
        //			patientObject.put("dob", this.getVisit().getPatient().getBirthDateTime());
        //		}

        List<HashMap<String, Object>> patientIdentifiers = new ArrayList<HashMap<String, Object>>();
        for (PatientIdentifier patientIdentifier : this.getVisit().getPatient().getIdentifiers()) {

            HashMap<String, Object> patientIdentifierObject = new HashMap<String, Object>();
            patientIdentifierObject.put("id", patientIdentifier.getIdentifier());
            patientIdentifierObject.put("name", patientIdentifier.getIdentifierType().getName());

            patientIdentifiers.add(patientIdentifierObject);
        }


        String phoneNumber = null;
        String phoneAttributeTypeUuid = Context.getService(AdministrationService.class).getGlobalProperty(ICareConfig.PHONE_NUMBER_ATTRIBUTE);
        if (phoneAttributeTypeUuid != null) {
            phoneNumber = this.getVisit().getPatient().getPerson().getAttribute(Context.getPersonService().getPersonAttributeTypeByUuid(phoneAttributeTypeUuid)).getValue();
        }
        patientObject.put("phone", phoneNumber);

        patientObject.put("identifiers", patientIdentifiers);
        patientObject.put("age", this.getVisit().getPatient().getAge());
        patientObject.put("familyName", this.getVisit().getPatient().getPersonName().getFamilyName());
        patientObject.put("middleName", this.getVisit().getPatient().getPersonName().getMiddleName());
        patientObject.put("givenName", this.getVisit().getPatient().getPersonName().getGivenName());
        patientObject.put("gender", this.getVisit().getPatient().getGender());
        patientObject.put("uuid", this.getVisit().getPatient().getUuid());


        List<Map<String, Object>> attributes = new ArrayList<>();
        if (this.getVisit().getPatient().getPerson().getAttributes().size() > 0) {
            for (PersonAttribute personAttribute: this.getVisit().getPatient().getPerson().getAttributes()) {
                Map<String, Object> attribute = new HashMap<>();
                Map<String, Object> attributeType = new HashMap<>();
                attributeType.put("uuid", personAttribute.getAttributeType().getUuid());
                attribute.put("attributeType", attributeType);
                attribute.put("value", personAttribute.getValue());
            }
        }
        patientObject.put("attributes", attributes);

        List<Map<String, Object>> addresses = new ArrayList<>();
        if (this.getVisit().getPatient().getPerson().getAddresses().size() > 0) {
            for(PersonAddress personAddress: this.getVisit().getPatient().getPerson().getAddresses()) {
                Map<String, Object> address = new HashMap<>();
                address.put("address1", personAddress.getAddress1().toString());
                address.put("address2", personAddress.getAddress2().toString());
                address.put("address3", personAddress.getAddress3().toString());
                address.put("address4", personAddress.getAddress4().toString());
                address.put("cityVillage", personAddress.getCityVillage());
                address.put("country", personAddress.getCountry());
                addresses.add(address);
            }
        }
        patientObject.put("addresses", addresses);

        sampleObject.put("patient", patientObject);

        if(this.getBatchSample() != null){
            Map<String,Object> batchObject = new HashMap<>();
            batchObject.put("uuid",this.getBatchSample().getUuid());
            batchObject.put("display",this.getBatchSample().getCode());
            sampleObject.put("batch",batchObject);
        }

        return sampleObject;
    }
}
