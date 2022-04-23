package mysql;

import clases.Airport;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MainVerticleAirport extends AbstractVerticle{
	
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
			getById_Airport("1");	
		}

	}
	
	private void getAll() {
		mySqlClient.query("SELECT * FROM dad_db_avion.airport;").execute(res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
							 elem.getDouble("lat"), elem.getDouble("lon"))));
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
				connection.result().query("SELECT * FROM dad_db_avion.airport;").execute( res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
											 elem.getDouble("lat"), elem.getDouble("lon"))));
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

	private void getById_Airport(String id_Airport) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM dad_db_avion.airport WHERE id_Airport = ?").execute(
						Tuple.of(id_Airport), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
											 elem.getDouble("lat"), elem.getDouble("lon"))));
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
