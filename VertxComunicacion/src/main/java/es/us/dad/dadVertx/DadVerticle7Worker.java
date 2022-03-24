package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle7Worker extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {
		// Aquí ejecutamos el código bloqueante
		try {
			Thread.sleep(5000);
			startFuture.complete();
		} catch (Exception e) {
			e.printStackTrace();
			startFuture.fail(e.getMessage());
		}
		startFuture.complete();
	}
}


