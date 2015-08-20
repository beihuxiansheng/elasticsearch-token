begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|RandomAccessWeight
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Weight
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoPoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexGeoPointFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|MultiGeoPointValues
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GeoPolygonQuery
specifier|public
class|class
name|GeoPolygonQuery
extends|extends
name|Query
block|{
DECL|field|points
specifier|private
specifier|final
name|GeoPoint
index|[]
name|points
decl_stmt|;
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexGeoPointFieldData
name|indexFieldData
decl_stmt|;
DECL|method|GeoPolygonQuery
specifier|public
name|GeoPolygonQuery
parameter_list|(
name|IndexGeoPointFieldData
name|indexFieldData
parameter_list|,
name|GeoPoint
modifier|...
name|points
parameter_list|)
block|{
name|this
operator|.
name|points
operator|=
name|points
expr_stmt|;
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
block|}
DECL|method|points
specifier|public
name|GeoPoint
index|[]
name|points
parameter_list|()
block|{
return|return
name|points
return|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RandomAccessWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Bits
name|getMatchingDocs
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|MultiGeoPointValues
name|values
init|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getGeoPointValues
argument_list|()
decl_stmt|;
return|return
operator|new
name|Bits
argument_list|()
block|{
specifier|private
name|boolean
name|pointInPolygon
parameter_list|(
name|GeoPoint
index|[]
name|points
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|boolean
name|inPoly
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|points
index|[
name|i
index|]
operator|.
name|lon
argument_list|()
operator|<
name|lon
operator|&&
name|points
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|lon
argument_list|()
operator|>=
name|lon
operator|||
name|points
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|lon
argument_list|()
operator|<
name|lon
operator|&&
name|points
index|[
name|i
index|]
operator|.
name|lon
argument_list|()
operator|>=
name|lon
condition|)
block|{
if|if
condition|(
name|points
index|[
name|i
index|]
operator|.
name|lat
argument_list|()
operator|+
operator|(
name|lon
operator|-
name|points
index|[
name|i
index|]
operator|.
name|lon
argument_list|()
operator|)
operator|/
operator|(
name|points
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|lon
argument_list|()
operator|-
name|points
index|[
name|i
index|]
operator|.
name|lon
argument_list|()
operator|)
operator|*
operator|(
name|points
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|lat
argument_list|()
operator|-
name|points
index|[
name|i
index|]
operator|.
name|lat
argument_list|()
operator|)
operator|<
name|lat
condition|)
block|{
name|inPoly
operator|=
operator|!
name|inPoly
expr_stmt|;
block|}
block|}
block|}
return|return
name|inPoly
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|GeoPoint
name|point
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|pointInPolygon
argument_list|(
name|points
argument_list|,
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"GeoPolygonFilter("
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|points
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|GeoPolygonQuery
name|that
init|=
operator|(
name|GeoPolygonQuery
operator|)
name|obj
decl_stmt|;
return|return
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|points
argument_list|,
name|that
operator|.
name|points
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|points
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit
