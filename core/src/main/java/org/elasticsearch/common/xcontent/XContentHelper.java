begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|collect
operator|.
name|Tuple
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
name|compress
operator|.
name|Compressor
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
name|compress
operator|.
name|CompressorFactory
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
name|ToXContent
operator|.
name|Params
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
operator|.
name|EMPTY_PARAMS
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|XContentHelper
specifier|public
class|class
name|XContentHelper
block|{
DECL|method|createParser
specifier|public
specifier|static
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|compressedInput
init|=
name|compressor
operator|.
name|streamInput
argument_list|(
name|bytes
operator|.
name|streamInput
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressedInput
operator|.
name|markSupported
argument_list|()
operator|==
literal|false
condition|)
block|{
name|compressedInput
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|compressedInput
argument_list|)
expr_stmt|;
block|}
name|XContentType
name|contentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|compressedInput
argument_list|)
decl_stmt|;
return|return
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|xContentRegistry
argument_list|,
name|compressedInput
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|bytes
argument_list|)
operator|.
name|createParser
argument_list|(
name|xContentRegistry
argument_list|,
name|bytes
operator|.
name|streamInput
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|convertToMap
specifier|public
specifier|static
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|convertToMap
parameter_list|(
name|BytesReference
name|bytes
parameter_list|,
name|boolean
name|ordered
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
try|try
block|{
name|XContentType
name|contentType
decl_stmt|;
name|InputStream
name|input
decl_stmt|;
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|compressedStreamInput
init|=
name|compressor
operator|.
name|streamInput
argument_list|(
name|bytes
operator|.
name|streamInput
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressedStreamInput
operator|.
name|markSupported
argument_list|()
operator|==
literal|false
condition|)
block|{
name|compressedStreamInput
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|compressedStreamInput
argument_list|)
expr_stmt|;
block|}
name|contentType
operator|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|compressedStreamInput
argument_list|)
expr_stmt|;
name|input
operator|=
name|compressedStreamInput
expr_stmt|;
block|}
else|else
block|{
name|contentType
operator|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|input
operator|=
name|bytes
operator|.
name|streamInput
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|contentType
argument_list|,
name|convertToMap
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentType
argument_list|)
argument_list|,
name|input
argument_list|,
name|ordered
argument_list|)
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
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse content to map"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Convert a string in some {@link XContent} format to a {@link Map}. Throws an {@link ElasticsearchParseException} if there is any      * error.      */
DECL|method|convertToMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertToMap
parameter_list|(
name|XContent
name|xContent
parameter_list|,
name|String
name|string
parameter_list|,
name|boolean
name|ordered
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
comment|// It is safe to use EMPTY here because this never uses namedObject
try|try
init|(
name|XContentParser
name|parser
init|=
name|xContent
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|string
argument_list|)
init|)
block|{
return|return
name|ordered
condition|?
name|parser
operator|.
name|mapOrdered
argument_list|()
else|:
name|parser
operator|.
name|map
argument_list|()
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
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse content to map"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Convert a string in some {@link XContent} format to a {@link Map}. Throws an {@link ElasticsearchParseException} if there is any      * error. Note that unlike {@link #convertToMap(BytesReference, boolean)}, this doesn't automatically uncompress the input.      */
DECL|method|convertToMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertToMap
parameter_list|(
name|XContent
name|xContent
parameter_list|,
name|InputStream
name|input
parameter_list|,
name|boolean
name|ordered
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
comment|// It is safe to use EMPTY here because this never uses namedObject
try|try
init|(
name|XContentParser
name|parser
init|=
name|xContent
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|input
argument_list|)
init|)
block|{
return|return
name|ordered
condition|?
name|parser
operator|.
name|mapOrdered
argument_list|()
else|:
name|parser
operator|.
name|map
argument_list|()
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
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse content to map"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|convertToJson
specifier|public
specifier|static
name|String
name|convertToJson
parameter_list|(
name|BytesReference
name|bytes
parameter_list|,
name|boolean
name|reformatJson
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|convertToJson
argument_list|(
name|bytes
argument_list|,
name|reformatJson
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|convertToJson
specifier|public
specifier|static
name|String
name|convertToJson
parameter_list|(
name|BytesReference
name|bytes
parameter_list|,
name|boolean
name|reformatJson
parameter_list|,
name|boolean
name|prettyPrint
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentType
name|xContentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|xContentType
operator|==
name|XContentType
operator|.
name|JSON
operator|&&
operator|!
name|reformatJson
condition|)
block|{
return|return
name|bytes
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
comment|// It is safe to use EMPTY here because this never uses namedObject
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|xContentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|bytes
operator|.
name|streamInput
argument_list|()
argument_list|)
init|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|prettyPrint
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
block|}
comment|/**      * Writes serialized toXContent to pretty-printed JSON string.      *      * @param toXContent object to be pretty printed      * @return pretty-printed JSON serialization      */
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|ToXContent
name|toXContent
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|toXContent
argument_list|,
name|EMPTY_PARAMS
argument_list|)
return|;
block|}
comment|/**      * Writes serialized toXContent to pretty-printed JSON string.      *      * @param toXContent object to be pretty printed      * @param params     serialization parameters      * @return pretty-printed JSON serialization      */
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|ToXContent
name|toXContent
parameter_list|,
name|Params
name|params
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"pretty"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"human"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|builder
operator|.
name|humanReadable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"error"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"cannot generate error message for deserialization"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Updates the provided changes into the source. If the key exists in the changes, it overrides the one in source      * unless both are Maps, in which case it recuersively updated it.      *      * @param source                 the original map to be updated      * @param changes                the changes to update into updated      * @param checkUpdatesAreUnequal should this method check if updates to the same key (that are not both maps) are      *                               unequal?  This is just a .equals check on the objects, but that can take some time on long strings.      * @return true if the source map was modified      */
DECL|method|update
specifier|public
specifier|static
name|boolean
name|update
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|changes
parameter_list|,
name|boolean
name|checkUpdatesAreUnequal
parameter_list|)
block|{
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|changesEntry
range|:
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|source
operator|.
name|containsKey
argument_list|(
name|changesEntry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// safe to copy, change does not exist in source
name|source
operator|.
name|put
argument_list|(
name|changesEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|changesEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|modified
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
name|Object
name|old
init|=
name|source
operator|.
name|get
argument_list|(
name|changesEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|instanceof
name|Map
operator|&&
name|changesEntry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
comment|// recursive merge maps
name|modified
operator||=
name|update
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|source
operator|.
name|get
argument_list|(
name|changesEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|changesEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|checkUpdatesAreUnequal
operator|&&
operator|!
name|modified
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// update the field
name|source
operator|.
name|put
argument_list|(
name|changesEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|changesEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|modified
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|checkUpdatesAreUnequal
condition|)
block|{
name|modified
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
name|modified
operator|=
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|old
argument_list|,
name|changesEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|modified
return|;
block|}
comment|/**      * Merges the defaults provided as the second parameter into the content of the first. Only does recursive merge      * for inner maps.      */
DECL|method|mergeDefaults
specifier|public
specifier|static
name|void
name|mergeDefaults
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|content
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|defaults
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|defaultEntry
range|:
name|defaults
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|content
operator|.
name|containsKey
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// copy it over, it does not exists in the content
name|content
operator|.
name|put
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|defaultEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// in the content and in the default, only merge compound ones (maps)
if|if
condition|(
name|content
operator|.
name|get
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|instanceof
name|Map
operator|&&
name|defaultEntry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
name|mergeDefaults
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|content
operator|.
name|get
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|defaultEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|get
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|instanceof
name|List
operator|&&
name|defaultEntry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|List
condition|)
block|{
name|List
name|defaultList
init|=
operator|(
name|List
operator|)
name|defaultEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
name|contentList
init|=
operator|(
name|List
operator|)
name|content
operator|.
name|get
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|List
name|mergedList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|allListValuesAreMapsOfOne
argument_list|(
name|defaultList
argument_list|)
operator|&&
name|allListValuesAreMapsOfOne
argument_list|(
name|contentList
argument_list|)
condition|)
block|{
comment|// all are in the form of [ {"key1" : {}}, {"key2" : {}} ], merge based on keys
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|processed
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|contentList
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|processed
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|o
range|:
name|defaultList
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|processed
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|mergeDefaults
argument_list|(
name|processed
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// put the default entries after the content ones.
name|processed
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
range|:
name|processed
operator|.
name|values
argument_list|()
control|)
block|{
name|mergedList
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// if both are lists, simply combine them, first the defaults, then the content
comment|// just make sure not to add the same value twice
name|mergedList
operator|.
name|addAll
argument_list|(
name|defaultList
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|contentList
control|)
block|{
if|if
condition|(
operator|!
name|mergedList
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|mergedList
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|content
operator|.
name|put
argument_list|(
name|defaultEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|mergedList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|allListValuesAreMapsOfOne
specifier|private
specifier|static
name|boolean
name|allListValuesAreMapsOfOne
parameter_list|(
name|List
name|list
parameter_list|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Map
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
operator|(
name|Map
operator|)
name|o
operator|)
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Low level implementation detail of {@link XContentGenerator#copyCurrentStructure(XContentParser)}.      */
DECL|method|copyCurrentStructure
specifier|public
specifier|static
name|void
name|copyCurrentStructure
parameter_list|(
name|XContentGenerator
name|destination
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
comment|// Let's handle field-name separately first
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|destination
operator|.
name|writeFieldName
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// fall-through to copy the associated value
block|}
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|START_ARRAY
case|:
name|destination
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|copyCurrentStructure
argument_list|(
name|destination
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
name|destination
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
break|break;
case|case
name|START_OBJECT
case|:
name|destination
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
name|copyCurrentStructure
argument_list|(
name|destination
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
name|destination
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
break|break;
default|default:
comment|// others are simple:
name|copyCurrentEvent
argument_list|(
name|destination
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copyCurrentEvent
specifier|public
specifier|static
name|void
name|copyCurrentEvent
parameter_list|(
name|XContentGenerator
name|generator
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
condition|)
block|{
case|case
name|START_OBJECT
case|:
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
break|break;
case|case
name|END_OBJECT
case|:
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
break|break;
case|case
name|START_ARRAY
case|:
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
break|break;
case|case
name|END_ARRAY
case|:
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
break|break;
case|case
name|FIELD_NAME
case|:
name|generator
operator|.
name|writeFieldName
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|VALUE_STRING
case|:
if|if
condition|(
name|parser
operator|.
name|hasTextCharacters
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|parser
operator|.
name|textCharacters
argument_list|()
argument_list|,
name|parser
operator|.
name|textOffset
argument_list|()
argument_list|,
name|parser
operator|.
name|textLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|VALUE_NUMBER
case|:
switch|switch
condition|(
name|parser
operator|.
name|numberType
argument_list|()
condition|)
block|{
case|case
name|INT
case|:
name|generator
operator|.
name|writeNumber
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|generator
operator|.
name|writeNumber
argument_list|(
name|parser
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|generator
operator|.
name|writeNumber
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|generator
operator|.
name|writeNumber
argument_list|(
name|parser
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
name|VALUE_BOOLEAN
case|:
name|generator
operator|.
name|writeBoolean
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|VALUE_NULL
case|:
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
break|break;
case|case
name|VALUE_EMBEDDED_OBJECT
case|:
name|generator
operator|.
name|writeBinary
argument_list|(
name|parser
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Writes a "raw" (bytes) field, handling cases where the bytes are compressed, and tries to optimize writing using      * {@link XContentBuilder#rawField(String, org.elasticsearch.common.bytes.BytesReference)}.      */
DECL|method|writeRawField
specifier|public
specifier|static
name|void
name|writeRawField
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesReference
name|source
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|compressedStreamInput
init|=
name|compressor
operator|.
name|streamInput
argument_list|(
name|source
operator|.
name|streamInput
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|rawField
argument_list|(
name|field
argument_list|,
name|compressedStreamInput
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|rawField
argument_list|(
name|field
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the bytes that represent the XContent output of the provided {@link ToXContent} object, using the provided      * {@link XContentType}. Wraps the output into a new anonymous object.      */
DECL|method|toXContent
specifier|public
specifier|static
name|BytesReference
name|toXContent
parameter_list|(
name|ToXContent
name|toXContent
parameter_list|,
name|XContentType
name|xContentType
parameter_list|,
name|boolean
name|humanReadable
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|xContentType
operator|.
name|xContent
argument_list|()
argument_list|)
init|)
block|{
name|builder
operator|.
name|humanReadable
argument_list|(
name|humanReadable
argument_list|)
expr_stmt|;
if|if
condition|(
name|toXContent
operator|.
name|isFragment
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|}
name|toXContent
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
if|if
condition|(
name|toXContent
operator|.
name|isFragment
argument_list|()
condition|)
block|{
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|bytes
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

