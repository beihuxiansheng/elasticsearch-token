begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex.remote
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ByteArrayEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|StringEntity
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
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchRequest
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
name|Strings
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
name|bytes
operator|.
name|BytesReference
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
name|TimeValue
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
name|xcontent
operator|.
name|NamedXContentRegistry
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentHelper
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
name|xcontent
operator|.
name|XContentParser
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
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|FieldSortBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|SortBuilder
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_class
DECL|class|RemoteRequestBuilders
specifier|final
class|class
name|RemoteRequestBuilders
block|{
DECL|method|RemoteRequestBuilders
specifier|private
name|RemoteRequestBuilders
parameter_list|()
block|{}
DECL|method|initialSearchPath
specifier|static
name|String
name|initialSearchPath
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|)
block|{
comment|// It is nasty to build paths with StringBuilder but we'll be careful....
name|StringBuilder
name|path
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|addIndexesOrTypes
argument_list|(
name|path
argument_list|,
literal|"Index"
argument_list|,
name|searchRequest
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|addIndexesOrTypes
argument_list|(
name|path
argument_list|,
literal|"Type"
argument_list|,
name|searchRequest
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|path
operator|.
name|append
argument_list|(
literal|"_search"
argument_list|)
expr_stmt|;
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|initialSearchParams
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initialSearchParams
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|Version
name|remoteVersion
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchRequest
operator|.
name|scroll
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"scroll"
argument_list|,
name|searchRequest
operator|.
name|scroll
argument_list|()
operator|.
name|keepAlive
argument_list|()
operator|.
name|getStringRep
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|version
argument_list|()
operator|==
literal|null
operator|||
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|version
argument_list|()
operator|==
literal|true
condition|)
block|{
comment|// false is the only value that makes it false. Null defaults to true....
name|params
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|sorts
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|boolean
name|useScan
init|=
literal|false
decl_stmt|;
comment|// Detect if we should use search_type=scan rather than a sort
if|if
condition|(
name|remoteVersion
operator|.
name|before
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
literal|2010099
argument_list|)
argument_list|)
condition|)
block|{
for|for
control|(
name|SortBuilder
argument_list|<
name|?
argument_list|>
name|sort
range|:
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|sorts
argument_list|()
control|)
block|{
if|if
condition|(
name|sort
operator|instanceof
name|FieldSortBuilder
condition|)
block|{
name|FieldSortBuilder
name|f
init|=
operator|(
name|FieldSortBuilder
operator|)
name|sort
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|getFieldName
argument_list|()
operator|.
name|equals
argument_list|(
name|FieldSortBuilder
operator|.
name|DOC_FIELD_NAME
argument_list|)
condition|)
block|{
name|useScan
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
name|useScan
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"search_type"
argument_list|,
literal|"scan"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|sorts
init|=
operator|new
name|StringBuilder
argument_list|(
name|sortToUri
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
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
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|sorts
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sorts
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|sortToUri
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
name|sorts
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|remoteVersion
operator|.
name|before
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
literal|2000099
argument_list|)
argument_list|)
condition|)
block|{
comment|// Versions before 2.0.0 need prompting to return interesting fields. Note that timestamp isn't available at all....
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|storedField
argument_list|(
literal|"_parent"
argument_list|)
operator|.
name|storedField
argument_list|(
literal|"_routing"
argument_list|)
operator|.
name|storedField
argument_list|(
literal|"_ttl"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|storedFields
argument_list|()
operator|!=
literal|null
operator|&&
literal|false
operator|==
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|storedFields
argument_list|()
operator|.
name|fieldNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|fields
init|=
operator|new
name|StringBuilder
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|storedFields
argument_list|()
operator|.
name|fieldNames
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
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
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|storedFields
argument_list|()
operator|.
name|fieldNames
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|fields
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|storedFields
argument_list|()
operator|.
name|fieldNames
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|storedFieldsParamName
init|=
name|remoteVersion
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_5_0_0_alpha4
argument_list|)
condition|?
literal|"fields"
else|:
literal|"stored_fields"
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|storedFieldsParamName
argument_list|,
name|fields
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
DECL|method|initialSearchEntity
specifier|static
name|HttpEntity
name|initialSearchEntity
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|BytesReference
name|query
parameter_list|)
block|{
comment|// EMPTY is safe here because we're not calling namedObject
try|try
init|(
name|XContentBuilder
name|entity
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
init|;
name|XContentParser
name|queryParser
operator|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|query
argument_list|)
init|)
block|{
name|entity
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|entity
operator|.
name|field
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
block|{
comment|/* We're intentionally a bit paranoid here - copying the query as xcontent rather than writing a raw field. We don't want                  * poorly written queries to escape. Ever. */
name|entity
operator|.
name|copyCurrentStructure
argument_list|(
name|queryParser
argument_list|)
expr_stmt|;
name|XContentParser
operator|.
name|Token
name|shouldBeEof
init|=
name|queryParser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldBeEof
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"query was more than a single object. This first token after the object is ["
operator|+
name|shouldBeEof
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|fetchSource
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|entity
operator|.
name|field
argument_list|(
literal|"_source"
argument_list|,
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|fetchSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entity
operator|.
name|field
argument_list|(
literal|"_source"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|entity
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|BytesRef
name|bytes
init|=
name|entity
operator|.
name|bytes
argument_list|()
operator|.
name|toBytesRef
argument_list|()
decl_stmt|;
return|return
operator|new
name|ByteArrayEntity
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"unexpected error building entity"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|addIndexesOrTypes
specifier|private
specifier|static
name|void
name|addIndexesOrTypes
parameter_list|(
name|StringBuilder
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
index|[]
name|indicesOrTypes
parameter_list|)
block|{
if|if
condition|(
name|indicesOrTypes
operator|==
literal|null
operator|||
name|indicesOrTypes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|indexOrType
range|:
name|indicesOrTypes
control|)
block|{
name|checkIndexOrType
argument_list|(
name|name
argument_list|,
name|indexOrType
argument_list|)
expr_stmt|;
block|}
name|path
operator|.
name|append
argument_list|(
name|Strings
operator|.
name|arrayToCommaDelimitedString
argument_list|(
name|indicesOrTypes
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
DECL|method|checkIndexOrType
specifier|private
specifier|static
name|void
name|checkIndexOrType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexOrType
parameter_list|)
block|{
if|if
condition|(
name|indexOrType
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|name
operator|+
literal|" containing [,] not supported but got ["
operator|+
name|indexOrType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexOrType
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|name
operator|+
literal|" containing [/] not supported but got ["
operator|+
name|indexOrType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|sortToUri
specifier|private
specifier|static
name|String
name|sortToUri
parameter_list|(
name|SortBuilder
argument_list|<
name|?
argument_list|>
name|sort
parameter_list|)
block|{
if|if
condition|(
name|sort
operator|instanceof
name|FieldSortBuilder
condition|)
block|{
name|FieldSortBuilder
name|f
init|=
operator|(
name|FieldSortBuilder
operator|)
name|sort
decl_stmt|;
return|return
name|f
operator|.
name|getFieldName
argument_list|()
operator|+
literal|":"
operator|+
name|f
operator|.
name|order
argument_list|()
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported sort ["
operator|+
name|sort
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|scrollPath
specifier|static
name|String
name|scrollPath
parameter_list|()
block|{
return|return
literal|"/_search/scroll"
return|;
block|}
DECL|method|scrollParams
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|scrollParams
parameter_list|(
name|TimeValue
name|keepAlive
parameter_list|)
block|{
return|return
name|singletonMap
argument_list|(
literal|"scroll"
argument_list|,
name|keepAlive
operator|.
name|getStringRep
argument_list|()
argument_list|)
return|;
block|}
DECL|method|scrollEntity
specifier|static
name|HttpEntity
name|scrollEntity
parameter_list|(
name|String
name|scroll
parameter_list|,
name|Version
name|remoteVersion
parameter_list|)
block|{
if|if
condition|(
name|remoteVersion
operator|.
name|before
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
literal|2000099
argument_list|)
argument_list|)
condition|)
block|{
comment|// Versions before 2.0.0 extract the plain scroll_id from the body
return|return
operator|new
name|StringEntity
argument_list|(
name|scroll
argument_list|,
name|ContentType
operator|.
name|TEXT_PLAIN
argument_list|)
return|;
block|}
try|try
init|(
name|XContentBuilder
name|entity
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
init|)
block|{
return|return
operator|new
name|StringEntity
argument_list|(
name|entity
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"scroll_id"
argument_list|,
name|scroll
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to build scroll entity"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|clearScrollEntity
specifier|static
name|HttpEntity
name|clearScrollEntity
parameter_list|(
name|String
name|scroll
parameter_list|,
name|Version
name|remoteVersion
parameter_list|)
block|{
if|if
condition|(
name|remoteVersion
operator|.
name|before
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
literal|2000099
argument_list|)
argument_list|)
condition|)
block|{
comment|// Versions before 2.0.0 extract the plain scroll_id from the body
return|return
operator|new
name|StringEntity
argument_list|(
name|scroll
argument_list|,
name|ContentType
operator|.
name|TEXT_PLAIN
argument_list|)
return|;
block|}
try|try
init|(
name|XContentBuilder
name|entity
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
init|)
block|{
return|return
operator|new
name|StringEntity
argument_list|(
name|entity
operator|.
name|startObject
argument_list|()
operator|.
name|array
argument_list|(
literal|"scroll_id"
argument_list|,
name|scroll
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to build clear scroll entity"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

