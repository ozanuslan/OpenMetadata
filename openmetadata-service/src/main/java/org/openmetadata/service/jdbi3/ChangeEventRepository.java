/*
 *  Copyright 2021 Collate
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openmetadata.service.jdbi3;

import static org.openmetadata.schema.type.EventType.ENTITY_CREATED;
import static org.openmetadata.schema.type.EventType.ENTITY_DELETED;
import static org.openmetadata.schema.type.EventType.ENTITY_SOFT_DELETED;
import static org.openmetadata.schema.type.EventType.ENTITY_UPDATED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.openmetadata.schema.type.ChangeEvent;
import org.openmetadata.service.util.JsonUtils;

public class ChangeEventRepository {
  private final CollectionDAO.ChangeEventDAO dao;

  public ChangeEventRepository(CollectionDAO dao) {
    this.dao = dao.changeEventDAO();
  }

  @Transaction
  public List<ChangeEvent> list(
      long timestamp, List<String> entityCreatedList, List<String> entityUpdatedList, List<String> entityDeletedList)
      throws IOException {
    List<String> jsons = new ArrayList<>();
    jsons.addAll(dao.list(ENTITY_CREATED.value(), entityCreatedList, timestamp));
    jsons.addAll(dao.list(ENTITY_UPDATED.value(), entityUpdatedList, timestamp));
    jsons.addAll(dao.list(ENTITY_DELETED.value(), entityDeletedList, timestamp));
    jsons.addAll(dao.list(ENTITY_SOFT_DELETED.value(), entityDeletedList, timestamp));

    List<ChangeEvent> changeEvents = new ArrayList<>();
    for (String json : jsons) {
      changeEvents.add(JsonUtils.readValue(json, ChangeEvent.class));
    }
    return changeEvents;
  }

  @Transaction
  public void deleteAll(String entityType) throws IOException {
    dao.deleteAll(entityType);
  }
}
