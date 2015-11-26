/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.model.impl.operators.logicalgraph.unary;

import org.gradoop.model.api.EPGMEdge;
import org.gradoop.model.api.EPGMGraphHead;
import org.gradoop.model.api.EPGMVertex;
import org.gradoop.model.impl.LogicalGraph;
import org.gradoop.model.impl.functions.UnaryFunction;
import org.gradoop.model.api.operators.UnaryGraphToGraphOperator;

/**
 * Takes a logical graph and a user defined aggregate function as input. The
 * aggregate function is applied on the logical graph and the resulting
 * aggregate is stored as an additional property at the result graph.
 *
 * @param <VD> EPGM vertex type
 * @param <ED> EPGM edge type
 * @param <GD> EPGM graph head type
 * @param <O>  output type of aggregate function
 */
public class Aggregation<
  VD extends EPGMVertex,
  ED extends EPGMEdge,
  GD extends EPGMGraphHead,
  O extends Number>
  implements UnaryGraphToGraphOperator<VD, ED, GD> {

  /**
   * Used to store aggregate result.
   */
  private final String aggregatePropertyKey;
  /**
   * User defined aggregate function.
   */
  private final UnaryFunction<LogicalGraph<GD, VD, ED>, O> aggregationFunc;

  /**
   * Creates new aggregation.
   *
   * @param aggregatePropertyKey property key to store result of {@code
   *                             aggregationFunc}
   * @param aggregationFunc      user defined aggregation function which gets
   *                             called on the input graph
   */
  public Aggregation(final String aggregatePropertyKey,
    UnaryFunction<LogicalGraph<GD, VD, ED>, O> aggregationFunc) {
    this.aggregatePropertyKey = aggregatePropertyKey;
    this.aggregationFunc = aggregationFunc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LogicalGraph<GD, VD, ED> execute(LogicalGraph<GD, VD, ED> graph) throws
    Exception {
    O result = aggregationFunc.execute(graph);
    // copy graph data before updating properties
    GD newGraphData = graph.getConfig().getGraphHeadFactory()
      .createGraphHead(graph.getId(), graph.getLabel());
    newGraphData.setProperties(graph.getProperties());
    newGraphData.setProperty(aggregatePropertyKey, result);
    return LogicalGraph
      .fromDataSets(newGraphData, graph.getVertices(), graph.getEdges(), graph.getConfig());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return Aggregation.class.getName();
  }
}
