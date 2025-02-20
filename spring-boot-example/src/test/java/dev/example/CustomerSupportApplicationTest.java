package dev.example;

import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.slack.api.bolt.App;

@SpringBootTest
class CustomerSupportApplicationTest {

    @Autowired
    CustomerSupportAgent agent;

    @Test
    void should_provide_booking_details_and_explain_why_cancellation_is_not_possible() throws Exception {

        var appConfig = AppConfig.builder()
                .singleTeamBotToken("xoxb-")
                .build();
        var app = new App(appConfig);
        app.command("/hello", (req, ctx) -> {
            return ctx.ack(":wave: Hello!");
        });

        new SocketModeApp("xapp-", app).start();

        // Please define API keys in application.properties before running this test.
        // Tip: Use gpt-4 for this example, as gpt-3.5-turbo tends to hallucinate often and invent name and surname.

        interact(agent, "Hi, I forgot when my booking is.");
        interact(agent, "123-457");
        interact(agent, "I'm sorry I'm so inattentive today. Klaus Heisler.");
        interact(agent, "My bad, it's 123-456");

        // Here, information about the cancellation policy is automatically retrieved and injected into the prompt.
        // Although the LLM sometimes attempts to cancel the booking, it fails to do so and will explain
        // the reason why the booking cannot be cancelled, based on the injected cancellation policy.
        interact(agent, "My plans have changed, can I cancel my booking?");
    }

    private static void interact(CustomerSupportAgent agent, String userMessage) {
        System.out.println("==========================================================================================");
        System.out.println("[User]: " + userMessage);
        System.out.println("==========================================================================================");
        String agentAnswer = agent.chat(userMessage);
        System.out.println("==========================================================================================");
        System.out.println("[Agent]: " + agentAnswer);
        System.out.println("==========================================================================================");
    }
}
