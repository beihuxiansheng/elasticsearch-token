begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * A static factory for simple "import static" usage.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FilterBuilders
specifier|public
specifier|abstract
class|class
name|FilterBuilders
block|{
comment|/**      * A filter that matches all documents.      */
DECL|method|matchAllFilter
specifier|public
specifier|static
name|MatchAllFilterBuilder
name|matchAllFilter
parameter_list|()
block|{
return|return
operator|new
name|MatchAllFilterBuilder
argument_list|()
return|;
block|}
comment|/**      * Creates a new ids filter with the provided doc/mapping types.      *      * @param types The types to match the ids against.      */
DECL|method|idsFilter
specifier|public
specifier|static
name|IdsFilterBuilder
name|idsFilter
parameter_list|(
annotation|@
name|Nullable
name|String
modifier|...
name|types
parameter_list|)
block|{
return|return
operator|new
name|IdsFilterBuilder
argument_list|(
name|types
argument_list|)
return|;
block|}
comment|/**      * A filter based on doc/mapping type.      */
DECL|method|typeFilter
specifier|public
specifier|static
name|TypeFilterBuilder
name|typeFilter
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
operator|new
name|TypeFilterBuilder
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**      * A filter for a field based on a term.      *      * @param name  The field name      * @param value The term value      */
DECL|method|termFilter
specifier|public
specifier|static
name|TermFilterBuilder
name|termFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilterBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A filter for a field based on a term.      *      * @param name  The field name      * @param value The term value      */
DECL|method|termFilter
specifier|public
specifier|static
name|TermFilterBuilder
name|termFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilterBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A filter for a field based on a term.      *      * @param name  The field name      * @param value The term value      */
DECL|method|termFilter
specifier|public
specifier|static
name|TermFilterBuilder
name|termFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilterBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A filter for a field based on a term.      *      * @param name  The field name      * @param value The term value      */
DECL|method|termFilter
specifier|public
specifier|static
name|TermFilterBuilder
name|termFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilterBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A filter for a field based on a term.      *      * @param name  The field name      * @param value The term value      */
DECL|method|termFilter
specifier|public
specifier|static
name|TermFilterBuilder
name|termFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilterBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A filter for a field based on a term.      *      * @param name  The field name      * @param value The term value      */
DECL|method|termFilter
specifier|public
specifier|static
name|TermFilterBuilder
name|termFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilterBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|termsFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|termsFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|int
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|termsFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|long
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|termsFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|float
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|termsFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|double
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|termsFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|inFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|inFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|inFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|inFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|int
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|inFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|inFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|long
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|inFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|inFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|float
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|inFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|inFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|double
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|inFilter
specifier|public
specifier|static
name|TermsFilterBuilder
name|inFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsFilterBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filter that restricts search results to values that have a matching prefix in a given      * field.      *      * @param name   The field name      * @param prefix The prefix      */
DECL|method|prefixFilter
specifier|public
specifier|static
name|PrefixFilterBuilder
name|prefixFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|PrefixFilterBuilder
argument_list|(
name|name
argument_list|,
name|prefix
argument_list|)
return|;
block|}
comment|/**      * A filter that restricts search results to values that are within the given range.      *      * @param name The field name      */
DECL|method|rangeFilter
specifier|public
specifier|static
name|RangeFilterBuilder
name|rangeFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|RangeFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter that restricts search results to values that are within the given numeric range. Uses the      * field data cache (loading all the values for the specified field into memory)      *      * @param name The field name      */
DECL|method|numericRangeFilter
specifier|public
specifier|static
name|NumericRangeFilterBuilder
name|numericRangeFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter that simply wraps a query.      *      * @param queryBuilder The query to wrap as a filter      */
DECL|method|queryFilter
specifier|public
specifier|static
name|QueryFilterBuilder
name|queryFilter
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
operator|new
name|QueryFilterBuilder
argument_list|(
name|queryBuilder
argument_list|)
return|;
block|}
comment|/**      * A builder for filter based on a script.      *      * @param script The script to filter by.      */
DECL|method|scriptFilter
specifier|public
specifier|static
name|ScriptFilterBuilder
name|scriptFilter
parameter_list|(
name|String
name|script
parameter_list|)
block|{
return|return
operator|new
name|ScriptFilterBuilder
argument_list|(
name|script
argument_list|)
return|;
block|}
comment|/**      * A filter to filter based on a specific distance from a specific geo location / point.      *      * @param name The location field name.      */
DECL|method|geoDistanceFilter
specifier|public
specifier|static
name|GeoDistanceFilterBuilder
name|geoDistanceFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoDistanceFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter to filter based on a specific range from a specific geo location / point.      *      * @param name The location field name.      */
DECL|method|geoDistanceRangeFilter
specifier|public
specifier|static
name|GeoDistanceRangeFilterBuilder
name|geoDistanceRangeFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoDistanceRangeFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter to filter based on a bounding box defined by top left and bottom right locations / points      *      * @param name The location field name.      */
DECL|method|geoBoundingBoxFilter
specifier|public
specifier|static
name|GeoBoundingBoxFilterBuilder
name|geoBoundingBoxFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoBoundingBoxFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter to filter based on a polygon defined by a set of locations  / points.      *      * @param name The location field name.      */
DECL|method|geoPolygonFilter
specifier|public
specifier|static
name|GeoPolygonFilterBuilder
name|geoPolygonFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoPolygonFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter to filter only documents where a field exists in them.      *      * @param name The name of the field      */
DECL|method|existsFilter
specifier|public
specifier|static
name|ExistsFilterBuilder
name|existsFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ExistsFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A filter to filter only documents where a field does not exists in them.      *      * @param name The name of the field      */
DECL|method|missingFilter
specifier|public
specifier|static
name|MissingFilterBuilder
name|missingFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MissingFilterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Constructs a child filter, with the child type and the query to run against child documents, with      * the result of the filter being the *parent* documents.      *      * @param type  The child type      * @param query The query to run against the child type      */
DECL|method|hasChildFilter
specifier|public
specifier|static
name|HasChildFilterBuilder
name|hasChildFilter
parameter_list|(
name|String
name|type
parameter_list|,
name|XContentQueryBuilder
name|query
parameter_list|)
block|{
return|return
operator|new
name|HasChildFilterBuilder
argument_list|(
name|type
argument_list|,
name|query
argument_list|)
return|;
block|}
DECL|method|boolFilter
specifier|public
specifier|static
name|BoolFilterBuilder
name|boolFilter
parameter_list|()
block|{
return|return
operator|new
name|BoolFilterBuilder
argument_list|()
return|;
block|}
DECL|method|andFilter
specifier|public
specifier|static
name|AndFilterBuilder
name|andFilter
parameter_list|(
name|XContentFilterBuilder
modifier|...
name|filters
parameter_list|)
block|{
return|return
operator|new
name|AndFilterBuilder
argument_list|(
name|filters
argument_list|)
return|;
block|}
DECL|method|orFilter
specifier|public
specifier|static
name|OrFilterBuilder
name|orFilter
parameter_list|(
name|XContentFilterBuilder
modifier|...
name|filters
parameter_list|)
block|{
return|return
operator|new
name|OrFilterBuilder
argument_list|(
name|filters
argument_list|)
return|;
block|}
DECL|method|notFilter
specifier|public
specifier|static
name|NotFilterBuilder
name|notFilter
parameter_list|(
name|XContentFilterBuilder
name|filter
parameter_list|)
block|{
return|return
operator|new
name|NotFilterBuilder
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|FilterBuilders
specifier|private
name|FilterBuilders
parameter_list|()
block|{      }
block|}
end_class

end_unit

