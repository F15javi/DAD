package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle4ConsumerHeader extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startFuture) {
		getVertx().eventBus().consumer("mensaje-punto-a-punto", message -> {
			String customMessage = (String) message.body();
			System.out.println("Mensaje recibido (" + message.address() + "): " + customMessage);

			System.out.println(message.headers().toString());
			message.headers().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

			String replyMessage = "Sí, yo te he escuchado al mensaje \"" + message.body().toString() + "\"";
			message.reply(replyMessage);
		});
		startFuture.complete();
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
}


