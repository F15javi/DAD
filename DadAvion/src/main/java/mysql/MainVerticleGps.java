package mysql;

import io.vertx.core.AbstractVerticle;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import clases.Gps;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MainVerticleGps extends AbstractVerticle{

	MySQLPool mySqlClient;

	@Override
	public void start(Promise<Void> startFuture) {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad_db_avion").setUser("root").setPassword("rootroot");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		getAll();

		getAllWithConnection();

		
		for (int i = 0; i < 100; i++) {
			getById_Gps("1");	
		}

	}
	
	private void getAll() {
		mySqlClient.query("SELECT * FROM dad_db_avion.gps;").execute(res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
							elem.getDouble("lat"), elem.getDouble("lon"),
							elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("hor"))));
				}
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	private void getAllWithConnection() {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.gps;").execute( res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
											elem.getDouble("lat"), elem.getDouble("lon"),
											elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("hor"))));
						}
						System.out.println(result.toString());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	private void getById_Gps(String id_Gps) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM dad_db_avion.gps WHERE id_Gps = ?").execute(
						Tuple.of(id_Gps), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
											elem.getDouble("lat"), elem.getDouble("lon"), elem.getInteger("dir"), elem.getDouble("vel"), 
											elem.getDouble("alt"), elem.getLong("hor"))));
								}
								System.out.println(result.toString());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

	
}
