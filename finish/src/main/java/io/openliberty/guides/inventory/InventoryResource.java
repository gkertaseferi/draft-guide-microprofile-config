// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]

// tag::config-methods[]
package io.openliberty.guides.inventory;

import java.util.Properties;

// CDI
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.inventory.InventoryConfig;

@RequestScoped
@Path("systems")
public class InventoryResource {

  @Inject
  InventoryManager manager;

  // tag::config-injection[]
  @Inject
  InventoryConfig inventoryConfig;
  // end::config-injection[]

  @GET
  @Path("{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPropertiesForHost(@PathParam("hostname") String hostname) {

    if (!inventoryConfig.isInMaintenance()) {
      // tag::config-port[]
      Properties props = manager.get(hostname, inventoryConfig.getPortNumber());
      // end::config-port[]
      if (props == null) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(
                           "ERROR: Unknown hostname or the resource may not be running on the host machine")
                       .build();
      }
      return Response.ok(props).build();
    } else {
      // tag::email[]
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity(
                         "ERROR: Serive is currently in maintenance. Please contact: "
                             + inventoryConfig.getEmail().toString())
                     .build();
      // end::email[]
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response listContents() {
    if (!inventoryConfig.isInMaintenance()) {
      return Response.ok(manager.list()).build();
    } else {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity(
                         "ERROR: Serive is currently in maintenance. Please contact: "
                             + inventoryConfig.getEmail().toString())
                     .build();
    }
  }

}

// end::config-methods[]
