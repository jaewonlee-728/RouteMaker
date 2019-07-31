import java.util.HashMap;

import com.google.gson.Gson;

import spark.Response;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
		private Gson gson = new Gson();
		
		public String render(Object model) {
			if (model instanceof Response) {
				return gson.toJson(new HashMap<>());
			}
			return gson.toJson(model);
		}
	}