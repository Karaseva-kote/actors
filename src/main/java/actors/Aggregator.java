package actors;

import akka.actor.*;
import messages.ApiRequest;
import messages.Request;
import messages.Response;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static configuration.Config.TIMEOUT;

public class Aggregator extends UntypedActor {
	private final Map<String, List<String>> map;
	private final List<String> apiValues = List.of("yandex", "google", "bing");

	public Aggregator(Map<String, List<String>> map) {
		this.map = map;
	}

	@Override
	public void onReceive(Object o) throws Throwable {
		if (o instanceof Request) {
			String request = ((Request) o).getRequest();
			for(String api : apiValues) {
				getContext().actorOf(Props.create(ApiWorker.class), api).tell(new ApiRequest(api, request), getSelf());
			}
			getContext().setReceiveTimeout(Duration.create(TIMEOUT, TimeUnit.MILLISECONDS));
		}

		if (o instanceof Response) {
			Response r = (Response) o;
			map.put(r.getApiName(), r.getResponses());
			getSender().tell(PoisonPill.getInstance(), getSelf());
			if (map.size() > 2) {
				System.out.println(map);
				getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
			}
		}

		if (o instanceof ReceiveTimeout) {
			System.out.println("timeout");
			System.out.println(map);
			getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
		}
	}
}
