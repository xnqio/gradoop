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
package org.gradoop.flink.model.impl.operators.intersection;

import org.apache.flink.api.java.DataSet;
import org.gradoop.common.model.api.entities.Edge;
import org.gradoop.common.model.api.entities.GraphHead;
import org.gradoop.common.model.api.entities.Vertex;
import org.gradoop.flink.model.api.epgm.BaseGraph;
import org.gradoop.flink.model.api.epgm.BaseGraphCollection;
import org.gradoop.flink.model.impl.functions.epgm.Id;
import org.gradoop.flink.model.impl.functions.graphcontainment.GraphsContainmentFilterBroadcast;
import org.gradoop.flink.model.impl.functions.graphcontainment.InAnyGraphBroadcast;
import org.gradoop.common.model.impl.id.GradoopId;

/**
 * Returns a collection with all base graphs that exist in both input
 * collections. Graph equality is based on their identifiers.
 *
 * This operator implementation requires that a list of subgraph identifiers
 * in the resulting graph collections fits into the workers main memory.
 *
 * @param <G> type of the graph head
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <LG> type of the base graph instance
 * @param <GC> type of the graph collection
 */
public class IntersectionBroadcast<
  G extends GraphHead,
  V extends Vertex,
  E extends Edge,
  LG extends BaseGraph<G, V, E, LG, GC>,
  GC extends BaseGraphCollection<G, V, E, LG, GC>> extends Intersection<G, V, E, LG, GC> {

  @Override
  protected DataSet<V> computeNewVertices(DataSet<G> newSubgraphs) {

    DataSet<GradoopId> ids = secondCollection.getGraphHeads()
      .map(new Id<>());

    return firstCollection.getVertices()
      .filter(new InAnyGraphBroadcast<>())
      .withBroadcastSet(ids, GraphsContainmentFilterBroadcast.GRAPH_IDS);
  }
}
