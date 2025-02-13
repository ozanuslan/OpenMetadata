/*
 *  Copyright 2022 Collate
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

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmetadata.schema.api.services.CreateDashboardService.DashboardServiceType.Looker;

import org.openmetadata.schema.entity.services.DashboardService;
import org.openmetadata.schema.entity.services.ServiceType;
import org.openmetadata.schema.services.connections.database.MysqlConnection;
import org.openmetadata.schema.type.DashboardConnection;
import org.openmetadata.service.secrets.SecretsManager;

public class DashboardServiceRepositoryUnitTest
    extends ServiceEntityRepositoryTest<DashboardServiceRepository, DashboardService, DashboardConnection> {

  protected DashboardServiceRepositoryUnitTest() {
    super(ServiceType.DASHBOARD);
  }

  @Override
  protected DashboardServiceRepository newServiceRepository(
      CollectionDAO collectionDAO, SecretsManager secretsManager) {
    return new DashboardServiceRepository(collectionDAO, secretsManager);
  }

  @Override
  protected void mockServiceResourceSpecific() {
    service = mock(DashboardService.class);
    serviceConnection = mock(DashboardConnection.class);
    when(serviceConnection.getConfig()).thenReturn(mock(MysqlConnection.class));
    CollectionDAO.DashboardServiceDAO entityDAO = mock(CollectionDAO.DashboardServiceDAO.class);
    when(collectionDAO.dashboardServiceDAO()).thenReturn(entityDAO);
    when(entityDAO.getEntityClass()).thenReturn(DashboardService.class);
    when(service.withHref(isNull())).thenReturn(service);
    when(service.withOwner(isNull())).thenReturn(service);
    when(service.getConnection()).thenReturn(serviceConnection);
    when(service.getServiceType()).thenReturn(Looker);
  }
}
