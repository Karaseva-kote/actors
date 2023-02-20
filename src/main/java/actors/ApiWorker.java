package actors;

import akka.actor.UntypedActor;
import com.google.gson.Gson;
import messages.ApiRequest;
import messages.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static configuration.Config.TOP_N;

public class ApiWorker extends UntypedActor {
	@Override
	public void onReceive(Object o) throws Throwable {
		if (o instanceof ApiRequest) {
			ApiRequest r = (ApiRequest) o;
			URL url = new URL(r.getApiRequest());
			String json = "";
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
				json = reader.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return;
			}
			List<String> answers = new Gson().fromJson(json, Answers.class).answers
					.stream()
					.limit(TOP_N)
					.collect(Collectors.toList());
			getSender().tell(new Response(r.getApiName(), answers), getSelf());
		}
	}

	private static class Answers {
		public List<String> answers;
	}
}
