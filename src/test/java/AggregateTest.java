import com.xebialabs.restito.semantics.Condition;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import com.xebialabs.restito.server.StubServer;

import java.util.List;
import java.util.Map;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.*;
import static configuration.Config.PORT;
import static org.junit.Assert.assertEquals;

public class AggregateTest {
	private final Condition google = Condition.custom(call -> call.getUri().startsWith("/google"));
	private final Condition yandex = Condition.custom(call -> call.getUri().startsWith("/yandex"));
	private final Condition bing = Condition.custom(call -> call.getUri().startsWith("/bing"));
	private final String googleJson = "{ answers: [\"1\", \"2\", \"3\", \"4\", \"5\", \"6\"] }";
	private final String yandexJson = "{ answers: [\"meow\", \"mur\"] }";
	private final String bingJson = "{ answers: [\"я\", \"так\", \"устала\", \"господи\"] }";

	@Test
	public void simpleResponse() throws InterruptedException {
		StubServer server = new StubServer(PORT).run();
		whenHttp(server).match(google).then(stringContent(googleJson));
		whenHttp(server).match(yandex).then(stringContent(yandexJson));
		whenHttp(server).match(bing).then(stringContent(bingJson));
		Map<String, List<String>> answers = Run.aggregate("МЯУ!!!");
		Map<String, List<String>> expected = Map.of(
				"google", List.of("1", "2", "3", "4", "5"),
				"yandex", List.of("meow", "mur"),
				"bing", List.of("я", "так", "устала", "господи")
		);
		assertEquals(expected, answers);
		server.stop();
	}

	@Test
	public void timeoutResponse() throws InterruptedException {
		StubServer server = new StubServer(PORT).run();
		whenHttp(server).match(google).then(stringContent(googleJson));
		whenHttp(server).match(yandex).then(stringContent(yandexJson));
		whenHttp(server).match(bing).then(composite(delay(1500),stringContent(bingJson)));
		Map<String, List<String>> answers = Run.aggregate("МЯУ!!!");
		Map<String, List<String>> expected = Map.of(
				"google", List.of("1", "2", "3", "4", "5"),
				"yandex", List.of("meow", "mur")
		);
		assertEquals(expected, answers);
		server.stop();
	}

	@Test
	public void errorResponse() throws InterruptedException {
		StubServer server = new StubServer(PORT).run();
		whenHttp(server).match(google).then(status(HttpStatus.BAD_GATEWAY_502));
		whenHttp(server).match(yandex).then(stringContent(yandexJson));
		whenHttp(server).match(bing).then(stringContent(bingJson));
		Map<String, List<String>> answers = Run.aggregate("МЯУ!!!");
		Map<String, List<String>> expected = Map.of(
				"yandex", List.of("meow", "mur"),
				"bing", List.of("я", "так", "устала", "господи")
		);
		assertEquals(expected, answers);
		server.stop();
	}
}
