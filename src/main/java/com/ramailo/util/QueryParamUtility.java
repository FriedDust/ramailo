package com.ramailo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

public class QueryParamUtility {

	public static List<QueryParam> convert(MultivaluedMap<String, String> query) {
		List<QueryParam> params = new ArrayList<>();
		
		for (Entry<String, List<String>> entry : query.entrySet()) {
			String temp[] = entry.getKey().split("\\[");
			
			if (temp.length == 1) {
				for (String val : entry.getValue()) {
					QueryParam qp = new QueryParamUtility.QueryParam();
					qp.key = temp[0];
					qp.operator = "eq";
					qp.value = val;
					
					params.add(qp);
				}
			} else {
				String operator = temp[1].split("]")[0];
				
				for (String val : entry.getValue()) {
					QueryParam qp = new QueryParamUtility.QueryParam();
					qp.key = temp[0];
					qp.operator = operator;
					qp.value = val;
					
					params.add(qp);
				}
			}
		}
		
		return params;
	}

	public static class QueryParam {
		private String key;
		private String operator;
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}