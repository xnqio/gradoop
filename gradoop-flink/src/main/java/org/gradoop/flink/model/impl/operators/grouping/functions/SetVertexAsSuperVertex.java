/*
 * Copyright © 2014 - 2019 Leipzig University (Database Research Group)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradoop.flink.model.impl.operators.grouping.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.gradoop.common.model.api.entities.Vertex;
import org.gradoop.flink.model.impl.operators.grouping.tuples.VertexWithSuperVertex;

/**
 * Creates a {@link VertexWithSuperVertex} with both components referencing the same
 * vertex that is mapped on.
 *
 * @param <V> vertex type
 */
public class SetVertexAsSuperVertex<V extends Vertex> implements
  MapFunction<V, VertexWithSuperVertex> {

  /**
   * Avoid object instantiation.
   */
  private final VertexWithSuperVertex reuseTuple;

  /**
   * Constructor
   */
  public SetVertexAsSuperVertex() {
    reuseTuple = new VertexWithSuperVertex();
  }

  @Override
  public VertexWithSuperVertex map(V value) throws Exception {
    reuseTuple.setVertexId(value.getId());
    reuseTuple.setSuperVertexId(value.getId());
    return reuseTuple;
  }

}
