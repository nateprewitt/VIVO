/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.lang.String;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class PublicationHasAuthorValidator implements N3ValidatorVTwo {

    private static String MISSING_FIRST_NAME_ERROR = "Must specify the author's first name.";
    private static String MISSING_LAST_NAME_ERROR = "Must specify the author's last name.";
    private static String MALFORMED_LAST_NAME_ERROR = "Last name may not contain a comma. Please enter first name in first name field.";
;    
    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {
        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        List<String> personUri = urisFromForm.get("personUri");
        List<String> orgUri = urisFromForm.get("orgUri");
        List<Literal> orgNameList = literalsFromForm.get("orgName");
        
        if (allListElementsEmpty(personUri)) {
            personUri = null;
        }
        if (allListElementsEmpty(orgUri)) {
            orgUri = null;
        }
        Literal orgName = null;
        if(orgNameList != null && orgNameList.size() > 0) {
	        orgName = orgNameList.get(0);
        }
        // If there's a personUri, orgUri or orgName, then we're done. The firstName and lastName fields are
        // disabled and so don't get submitted.
        if (personUri != null || orgUri != null || orgName != null ) {
            return null;
        }
        
        //Expecting only one first name in this case
        //To Do: update logic if multiple first names considered
        Literal firstName = null;
        List<Literal> firstNameList = literalsFromForm.get("firstName");
        if(firstNameList != null && firstNameList.size() > 0) {
        	firstName = firstNameList.get(0);
        }
        if( firstName != null && 
        		firstName.getLexicalForm() != null && 
        		"".equals(firstName.getLexicalForm()) )
            firstName = null;


        List<Literal> lastNameList = literalsFromForm.get("lastName");
        Literal lastName = null;
        if(lastNameList != null && lastNameList.size() > 0) {
        	lastName = lastNameList.get(0);
        }
        String lastNameValue = "";
        if (lastName != null) {
            lastNameValue = lastName.getLexicalForm();
            if( "".equals(lastNameValue) ) {
                lastName = null;
            }
        }

        if (lastName == null) {
            errors.put("lastName", MISSING_LAST_NAME_ERROR);
        // Don't reject space in the last name: de Vries, etc.
        } else if (lastNameValue.contains(",")) {            
            errors.put("lastName", MALFORMED_LAST_NAME_ERROR);
        }
        
        if (firstName == null) {
            errors.put("firstName", MISSING_FIRST_NAME_ERROR);
        }       
        
        return errors.size() != 0 ? errors : null;
    }
    
    private boolean allListElementsEmpty(List<String> checkList) {
    	if(checkList == null)
    		return true;
    	if(checkList.isEmpty()) {
    		return true;
    	}
    	boolean allEmpty = true;
    	for(String s: checkList) {
    		if(s.length() != 0){
    			allEmpty = false;
    			break;
    		}
    	}
    	return allEmpty;
    }

}
