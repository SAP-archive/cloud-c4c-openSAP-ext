package com.sap.samples.odata;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.samples.Opportunity;

/**
 * The class wraps the deserialization from OData response JSON payload into Java object model.
 * The process is highly tailored to the use case of this particular application for simplicity reasons.
 * Normally, a fully featured OData client, such as Apache Olingo would be in this role.
 * 
 */
public class JsonFormat {
	
	/**
	 * Deserialize from C4C OData service response payload into local Java object model.
	 *  
	 * @param odataJsonPayload
	 * @return an Opportunity object deserialized form the JSON payload from the service
	 */
	public Opportunity fromJson(String odataJsonPayload){
		Gson gson = getGson();
		return gson.fromJson(odataJsonPayload, Opportunity.class);
	}

	class OpportunityODataPayloadDeserializer implements JsonDeserializer<Opportunity> {
		  public Opportunity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			  Opportunity deserialized = new Opportunity();
			  JsonArray resultsArr = json.getAsJsonObject()
					  				.get("d").getAsJsonObject()
					  				.get("results").getAsJsonArray();
			  Iterator<JsonElement> iter = resultsArr.iterator();
			  if(iter.hasNext()){
				  JsonObject result = iter.next().getAsJsonObject();
				  deserialized.statusCode = result.get("StatusCode").getAsString();
				  String expectedValueStr = result.get("ExpectedValue").getAsJsonObject().get("content").getAsString();
				  try {
					DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
					deserialized.expectedValue = (Long)formatter.parse(expectedValueStr);
				  } catch (ParseException e) {
					throw new JsonParseException(e);
				  }
				  deserialized.expectedValueCurrencyCode = result.get("ExpectedValue").getAsJsonObject().get("currencyCode").getAsString();
			  } else {
				  return null;
			  }
		    return deserialized;
		  }
	}
	
	private Gson getGson(){
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Opportunity.class, new OpportunityODataPayloadDeserializer());
		return gson.create();
	}
}
