package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle6BlockingCorrect extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {
		// Esta es la manera correcta de llamar a código bloqueante
		vertx.executeBlocking(future -> {
			try {
				Thread.sleep(5000);
				future.complete("Finalizado");
			} catch (Exception e) {
				e.printStackTrace();
				future.complete("Interrumpido");
			}
		}, res -> {
			System.out.println("El resultado es: " + res.result());
		});
	}
}


