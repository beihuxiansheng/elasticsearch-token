begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|analysis
operator|.
name|Analyzer
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
name|document
operator|.
name|FieldType
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
name|index
operator|.
name|Term
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
name|Filter
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
name|MultiTermQuery
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
name|util
operator|.
name|BytesRef
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
name|Nullable
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
name|unit
operator|.
name|Fuzziness
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
name|codec
operator|.
name|docvaluesformat
operator|.
name|DocValuesFormatProvider
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
name|codec
operator|.
name|postingsformat
operator|.
name|PostingsFormatProvider
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
name|FieldDataType
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
name|IndexFieldDataService
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
name|mapper
operator|.
name|core
operator|.
name|AbstractFieldMapper
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
name|query
operator|.
name|QueryParseContext
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
name|similarity
operator|.
name|SimilarityProvider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|FieldMapper
specifier|public
interface|interface
name|FieldMapper
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Mapper
block|{
DECL|field|DOC_VALUES_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|DOC_VALUES_FORMAT
init|=
literal|"doc_values_format"
decl_stmt|;
DECL|class|Names
specifier|public
specifier|static
class|class
name|Names
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|indexName
specifier|private
specifier|final
name|String
name|indexName
decl_stmt|;
DECL|field|indexNameClean
specifier|private
specifier|final
name|String
name|indexNameClean
decl_stmt|;
DECL|field|fullName
specifier|private
specifier|final
name|String
name|fullName
decl_stmt|;
DECL|field|sourcePath
specifier|private
specifier|final
name|String
name|sourcePath
decl_stmt|;
DECL|method|Names
specifier|public
name|Names
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|Names
specifier|public
name|Names
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|indexNameClean
parameter_list|,
name|String
name|fullName
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|indexNameClean
argument_list|,
name|fullName
argument_list|,
name|fullName
argument_list|)
expr_stmt|;
block|}
DECL|method|Names
specifier|public
name|Names
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|indexNameClean
parameter_list|,
name|String
name|fullName
parameter_list|,
annotation|@
name|Nullable
name|String
name|sourcePath
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|indexName
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexNameClean
operator|=
name|indexNameClean
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|fullName
operator|=
name|fullName
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourcePath
operator|=
name|sourcePath
operator|==
literal|null
condition|?
name|this
operator|.
name|fullName
else|:
name|sourcePath
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
comment|/**          * The logical name of the field.          */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**          * The indexed name of the field. This is the name under which we will          * store it in the index.          */
DECL|method|indexName
specifier|public
name|String
name|indexName
parameter_list|()
block|{
return|return
name|indexName
return|;
block|}
comment|/**          * The cleaned index name, before any "path" modifications performed on it.          */
DECL|method|indexNameClean
specifier|public
name|String
name|indexNameClean
parameter_list|()
block|{
return|return
name|indexNameClean
return|;
block|}
comment|/**          * The full name, including dot path.          */
DECL|method|fullName
specifier|public
name|String
name|fullName
parameter_list|()
block|{
return|return
name|fullName
return|;
block|}
comment|/**          * The dot path notation to extract the value from source.          */
DECL|method|sourcePath
specifier|public
name|String
name|sourcePath
parameter_list|()
block|{
return|return
name|sourcePath
return|;
block|}
comment|/**          * Creates a new index term based on the provided value.          */
DECL|method|createIndexNameTerm
specifier|public
name|Term
name|createIndexNameTerm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|indexName
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**          * Creates a new index term based on the provided value.          */
DECL|method|createIndexNameTerm
specifier|public
name|Term
name|createIndexNameTerm
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|indexName
argument_list|,
name|value
argument_list|)
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Names
name|names
init|=
operator|(
name|Names
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|fullName
operator|.
name|equals
argument_list|(
name|names
operator|.
name|fullName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|indexName
operator|.
name|equals
argument_list|(
name|names
operator|.
name|indexName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|indexNameClean
operator|.
name|equals
argument_list|(
name|names
operator|.
name|indexNameClean
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|names
operator|.
name|name
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|sourcePath
operator|.
name|equals
argument_list|(
name|names
operator|.
name|sourcePath
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|indexName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|indexNameClean
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fullName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|sourcePath
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|enum|Loading
specifier|public
specifier|static
enum|enum
name|Loading
block|{
DECL|enum constant|LAZY
name|LAZY
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|LAZY_VALUE
return|;
block|}
block|}
block|,
DECL|enum constant|EAGER
name|EAGER
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|EAGER_VALUE
return|;
block|}
block|}
block|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"loading"
decl_stmt|;
DECL|field|EAGER_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|EAGER_VALUE
init|=
literal|"eager"
decl_stmt|;
DECL|field|LAZY_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_VALUE
init|=
literal|"lazy"
decl_stmt|;
DECL|method|parse
specifier|public
specifier|static
name|Loading
name|parse
parameter_list|(
name|String
name|loading
parameter_list|,
name|Loading
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|loading
argument_list|)
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
elseif|else
if|if
condition|(
name|EAGER_VALUE
operator|.
name|equalsIgnoreCase
argument_list|(
name|loading
argument_list|)
condition|)
block|{
return|return
name|EAGER
return|;
block|}
elseif|else
if|if
condition|(
name|LAZY_VALUE
operator|.
name|equalsIgnoreCase
argument_list|(
name|loading
argument_list|)
condition|)
block|{
return|return
name|LAZY
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Unknown ["
operator|+
name|KEY
operator|+
literal|"] value: ["
operator|+
name|loading
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|names
name|Names
name|names
parameter_list|()
function_decl|;
DECL|method|fieldType
name|FieldType
name|fieldType
parameter_list|()
function_decl|;
DECL|method|boost
name|float
name|boost
parameter_list|()
function_decl|;
comment|/**      * The analyzer that will be used to index the field.      */
DECL|method|indexAnalyzer
name|Analyzer
name|indexAnalyzer
parameter_list|()
function_decl|;
comment|/**      * The analyzer that will be used to search the field.      */
DECL|method|searchAnalyzer
name|Analyzer
name|searchAnalyzer
parameter_list|()
function_decl|;
comment|/**      * The analyzer that will be used for quoted search on the field.      */
DECL|method|searchQuoteAnalyzer
name|Analyzer
name|searchQuoteAnalyzer
parameter_list|()
function_decl|;
comment|/**      * Similarity used for scoring queries on the field      */
DECL|method|similarity
name|SimilarityProvider
name|similarity
parameter_list|()
function_decl|;
comment|/**      * List of fields where this field should be copied to      */
DECL|method|copyTo
specifier|public
name|AbstractFieldMapper
operator|.
name|CopyTo
name|copyTo
parameter_list|()
function_decl|;
comment|/**      * Returns the actual value of the field.      */
DECL|method|value
name|T
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns the value that will be used as a result for search. Can be only of specific types... .      */
DECL|method|valueForSearch
name|Object
name|valueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns the indexed value used to construct search "values".      */
DECL|method|indexedValueForSearch
name|BytesRef
name|indexedValueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Should the field query {@link #termQuery(Object, org.elasticsearch.index.query.QueryParseContext)}  be used when detecting this      * field in query string.      */
DECL|method|useTermQueryWithQueryString
name|boolean
name|useTermQueryWithQueryString
parameter_list|()
function_decl|;
DECL|method|termQuery
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|termFilter
name|Filter
name|termFilter
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|termsFilter
name|Filter
name|termsFilter
parameter_list|(
name|List
name|values
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|termsFilter
name|Filter
name|termsFilter
parameter_list|(
name|IndexFieldDataService
name|fieldData
parameter_list|,
name|List
name|values
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|rangeQuery
name|Query
name|rangeQuery
parameter_list|(
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|rangeFilter
name|Filter
name|rangeFilter
parameter_list|(
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|fuzzyQuery
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|Fuzziness
name|fuzziness
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|,
name|boolean
name|transpositions
parameter_list|)
function_decl|;
DECL|method|prefixQuery
name|Query
name|prefixQuery
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|prefixFilter
name|Filter
name|prefixFilter
parameter_list|(
name|Object
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|regexpQuery
name|Query
name|regexpQuery
parameter_list|(
name|Object
name|value
parameter_list|,
name|int
name|flags
parameter_list|,
annotation|@
name|Nullable
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
function_decl|;
DECL|method|regexpFilter
name|Filter
name|regexpFilter
parameter_list|(
name|Object
name|value
parameter_list|,
name|int
name|flags
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|parseContext
parameter_list|)
function_decl|;
comment|/**      * A term query to use when parsing a query string. Can return<tt>null</tt>.      */
annotation|@
name|Nullable
DECL|method|queryStringTermQuery
name|Query
name|queryStringTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
function_decl|;
comment|/**      * Null value filter, returns<tt>null</tt> if there is no null value associated with the field.      */
annotation|@
name|Nullable
DECL|method|nullValueFilter
name|Filter
name|nullValueFilter
parameter_list|()
function_decl|;
DECL|method|fieldDataType
name|FieldDataType
name|fieldDataType
parameter_list|()
function_decl|;
DECL|method|postingsFormatProvider
name|PostingsFormatProvider
name|postingsFormatProvider
parameter_list|()
function_decl|;
DECL|method|docValuesFormatProvider
name|DocValuesFormatProvider
name|docValuesFormatProvider
parameter_list|()
function_decl|;
DECL|method|isNumeric
name|boolean
name|isNumeric
parameter_list|()
function_decl|;
DECL|method|isSortable
name|boolean
name|isSortable
parameter_list|()
function_decl|;
DECL|method|hasDocValues
name|boolean
name|hasDocValues
parameter_list|()
function_decl|;
DECL|method|normsLoading
name|Loading
name|normsLoading
parameter_list|(
name|Loading
name|defaultLoading
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

