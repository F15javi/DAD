package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle1 extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {
		vertx.createHttpServer().requestHandler(r -> {
			r.response().end("<h1>Bienvenido a mi primera aplicacion Vert.x 3</h1>"
					+ "Esto es un ejemplo de una Verticle sencillo para probar el despliegue");
		}).listen(8089, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
	}
}
