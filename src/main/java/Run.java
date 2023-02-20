import actors.Aggregator;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import messages.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static configuration.Config.TIMEOUT;

public class Run {
	public static Map<String, List<String>> aggregate(String request) throws InterruptedException {
		ActorSystem system = ActorSystem.create("aggregation");
		Map<String, List<String>> answers = new HashMap<>();
		final ActorRef kernel = system.actorOf(Props.create(Aggregator.class, answers), "aggregator");
		kernel.tell(new Request(request), ActorRef.noSender());
		Thread.sleep(TIMEOUT*2);
		return answers;
	}
}
