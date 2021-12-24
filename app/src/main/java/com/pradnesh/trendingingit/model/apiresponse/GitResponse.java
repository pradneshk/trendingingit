package com.pradnesh.trendingingit.model.apiresponse;

import java.util.List;

public class GitResponse {
     int total_count;
     boolean incomplete_results;
     List<Item> items;

     public int getTotal_count() {
          return total_count;
     }

     public void setTotal_count(int total_count) {
          this.total_count = total_count;
     }

     public boolean isIncomplete_results() {
          return incomplete_results;
     }

     public void setIncomplete_results(boolean incomplete_results) {
          this.incomplete_results = incomplete_results;
     }

     public List<Item> getItems() {
          return items;
     }

     public void setItems(List<Item> items) {
          this.items = items;
     }
}
