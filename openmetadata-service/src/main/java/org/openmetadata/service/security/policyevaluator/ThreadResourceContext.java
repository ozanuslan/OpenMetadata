package org.openmetadata.service.security.policyevaluator;

import java.io.IOException;
import java.util.List;
import org.openmetadata.schema.EntityInterface;
import org.openmetadata.schema.type.EntityReference;
import org.openmetadata.schema.type.TagLabel;
import org.openmetadata.service.Entity;

/** Conversation threads require special handling */
public class ThreadResourceContext implements ResourceContextInterface {
  private final EntityReference owner;

  public ThreadResourceContext(EntityReference owner) {
    this.owner = owner;
  }

  @Override
  public String getResource() {
    return Entity.THREAD;
  }

  @Override
  public EntityReference getOwner() throws IOException {
    return owner;
  }

  @Override
  public List<TagLabel> getTags() throws IOException {
    return null;
  }

  @Override
  public EntityInterface getEntity() throws IOException {
    return null;
  }
}
