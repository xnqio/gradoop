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
package org.gradoop.storage.hbase.impl.io;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.gradoop.common.model.impl.pojo.EPGMEdge;
import org.gradoop.common.model.impl.pojo.EPGMGraphHead;
import org.gradoop.common.model.impl.pojo.EPGMVertex;
import org.gradoop.flink.model.impl.epgm.GraphCollection;
import org.gradoop.flink.model.impl.epgm.GraphCollectionFactory;
import org.gradoop.flink.model.impl.epgm.LogicalGraph;
import org.gradoop.flink.model.impl.functions.tuple.ValueOf1;
import org.gradoop.flink.model.impl.operators.combination.ReduceCombination;
import org.gradoop.flink.util.GradoopFlinkConfig;
import org.gradoop.storage.common.io.FilterableDataSource;
import org.gradoop.storage.common.predicate.query.ElementQuery;
import org.gradoop.storage.hbase.impl.io.inputformats.EdgeTableInputFormat;
import org.gradoop.storage.hbase.impl.io.inputformats.GraphHeadTableInputFormat;
import org.gradoop.storage.hbase.impl.io.inputformats.VertexTableInputFormat;
import org.gradoop.storage.hbase.impl.predicate.filter.api.HBaseElementFilter;
import org.gradoop.storage.hbase.impl.HBaseEPGMStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Creates an EPGM instance from HBase.
 *
 * @see FilterableDataSource
 */
public class HBaseDataSource extends HBaseBase
  implements FilterableDataSource<
  ElementQuery<HBaseElementFilter<EPGMGraphHead>>,
  ElementQuery<HBaseElementFilter<EPGMVertex>>,
  ElementQuery<HBaseElementFilter<EPGMEdge>>> {

  /**
   * Query definition for graph head elements
   */
  private final ElementQuery<HBaseElementFilter<EPGMGraphHead>> graphHeadQuery;

  /**
   * Query definition for vertices
   */
  private final ElementQuery<HBaseElementFilter<EPGMVertex>> vertexQuery;

  /**
   * Query definition for edges
   */
  private final ElementQuery<HBaseElementFilter<EPGMEdge>> edgeQuery;

  /**
   * Creates a new HBase data source.
   *
   * @param epgmStore HBase store
   * @param flinkConfig gradoop flink execute config
   */
  public HBaseDataSource(
    @Nonnull HBaseEPGMStore epgmStore,
    @Nonnull GradoopFlinkConfig flinkConfig
  ) {
    this(epgmStore, flinkConfig, null, null, null);
  }

  /**
   * Private constructor to create a data source instance with predicates.
   *
   * @param epgmStore HBase store
   * @param flinkConfig gradoop flink execute config
   * @param graphHeadQuery A predicate to apply to graph head elements
   * @param vertexQuery A predicate to apply to vertices
   * @param edgeQuery A predicate to apply to edges
   */
  private HBaseDataSource(
    @Nonnull HBaseEPGMStore epgmStore,
    @Nonnull GradoopFlinkConfig flinkConfig,
    @Nullable ElementQuery<HBaseElementFilter<EPGMGraphHead>> graphHeadQuery,
    @Nullable ElementQuery<HBaseElementFilter<EPGMVertex>> vertexQuery,
    @Nullable ElementQuery<HBaseElementFilter<EPGMEdge>> edgeQuery
  ) {
    super(epgmStore, flinkConfig);
    this.graphHeadQuery = graphHeadQuery;
    this.vertexQuery = vertexQuery;
    this.edgeQuery = edgeQuery;
  }

  @Override
  public LogicalGraph getLogicalGraph() {
    return getGraphCollection().reduce(new ReduceCombination<>());
  }

  @Override
  public GraphCollection getGraphCollection() {
    GradoopFlinkConfig config = getFlinkConfig();
    GraphCollectionFactory factory = config.getGraphCollectionFactory();
    HBaseEPGMStore store = getStore();

    DataSet<EPGMGraphHead> graphHeads = config.getExecutionEnvironment()
      .createInput(new GraphHeadTableInputFormat(
          getHBaseConfig().getGraphHeadHandler().applyQuery(graphHeadQuery),
          store.getGraphHeadName()),
        new TupleTypeInfo<>(TypeExtractor.createTypeInfo(factory.getGraphHeadFactory().getType())))
      .map(new ValueOf1<>());

    DataSet<EPGMVertex> vertices = config.getExecutionEnvironment()
      .createInput(new VertexTableInputFormat(
          getHBaseConfig().getVertexHandler().applyQuery(vertexQuery),
          store.getVertexTableName()),
        new TupleTypeInfo<>(TypeExtractor.createTypeInfo(factory.getVertexFactory().getType())))
      .map(new ValueOf1<>());

    DataSet<EPGMEdge> edges = config.getExecutionEnvironment()
      .createInput(new EdgeTableInputFormat(
          getHBaseConfig().getEdgeHandler().applyQuery(edgeQuery),
          store.getEdgeTableName()),
        new TupleTypeInfo<>(TypeExtractor.createTypeInfo(factory.getEdgeFactory().getType())))
      .map(new ValueOf1<>());

    return factory.fromDataSets(graphHeads, vertices, edges);
  }

  @Nonnull
  @Override
  public HBaseDataSource applyGraphPredicate(
    @Nonnull ElementQuery<HBaseElementFilter<EPGMGraphHead>> query
  ) {
    return new HBaseDataSource(getStore(), getFlinkConfig(), query, vertexQuery, edgeQuery);
  }

  @Nonnull
  @Override
  public HBaseDataSource applyVertexPredicate(
    @Nonnull ElementQuery<HBaseElementFilter<EPGMVertex>> query
  ) {
    return new HBaseDataSource(getStore(), getFlinkConfig(), graphHeadQuery, query, edgeQuery);
  }

  @Nonnull
  @Override
  public HBaseDataSource applyEdgePredicate(
    @Nonnull ElementQuery<HBaseElementFilter<EPGMEdge>> query
  ) {
    return new HBaseDataSource(getStore(), getFlinkConfig(), graphHeadQuery, vertexQuery, query);
  }

  @Override
  public boolean isFilterPushedDown() {
    return this.graphHeadQuery != null || this.vertexQuery != null || this.edgeQuery != null;
  }
}
