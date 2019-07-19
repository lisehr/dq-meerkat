package dqm.jku.trustkg.demos.alex;

import java.io.IOException;

import com.appnexus.grafana.client.GrafanaClient;
import com.appnexus.grafana.client.models.Dashboard;
import com.appnexus.grafana.client.models.GrafanaDashboard;
import com.appnexus.grafana.configuration.GrafanaConfiguration;
import com.appnexus.grafana.exceptions.GrafanaException;

public class GrafanaDemo {
  private static final String APIKEY = "eyJrIjoiMmIwYlZZd0RwZkh4NXZZUXp6VTBid0ZwWlo3YmhMVHMiLCJuIjoiYmxvY2tkYXEiLCJpZCI6MX0=";
  
  public static void main(String args[]) throws GrafanaException, IOException {
  //Setup the client
    GrafanaConfiguration grafanaConfiguration =
            new GrafanaConfiguration().host("http://localhost:3000").apiKey(APIKEY);
    GrafanaClient grafanaClient = new GrafanaClient(grafanaConfiguration);

    //Setup the dashboard
    String DASHBOARD_NAME = "new_dashboard";

    Dashboard dashboard = new Dashboard()
          .title(DASHBOARD_NAME)
          .version(0);

    GrafanaDashboard grafanaDashboard = new GrafanaDashboard().dashboard(dashboard);

    //Make API calls
    grafanaClient.createDashboard(grafanaDashboard);

    grafanaClient.getDashboard(DASHBOARD_NAME);

    grafanaClient.deleteDashboard(DASHBOARD_NAME);
  }
}
