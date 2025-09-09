package com.dietiestates.shared.dto;

import java.util.List;

public class IdsRequest {
  private List<Long> ids;

  public IdsRequest() {}
  public IdsRequest(List<Long> ids) { this.ids = ids; }

  public List<Long> getIds() { return ids; }
  public void setIds(List<Long> ids) { this.ids = ids; }
}


///Users/utentenumero2/Desktop/DietiEstates25_demeo_carrese-main/backend_logic/shared/src/main/java/com/dietiestates/shared/dto/IdsRequest.java