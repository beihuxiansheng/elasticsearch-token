begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomNumbers
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
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
name|HashMap
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
DECL|class|RandomDocumentPicks
specifier|public
specifier|final
class|class
name|RandomDocumentPicks
block|{
DECL|method|RandomDocumentPicks
specifier|private
name|RandomDocumentPicks
parameter_list|()
block|{      }
comment|/**      * Returns a random field name. Can be a leaf field name or the      * path to refer to a field name using the dot notation.      */
DECL|method|randomFieldName
specifier|public
specifier|static
name|String
name|randomFieldName
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|int
name|numLevels
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|StringBuilder
name|fieldName
init|=
operator|new
name|StringBuilder
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
name|numLevels
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|fieldName
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
name|fieldName
operator|.
name|append
argument_list|(
name|randomString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldName
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns a random leaf field name.      */
DECL|method|randomLeafFieldName
specifier|public
specifier|static
name|String
name|randomLeafFieldName
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|String
name|fieldName
decl_stmt|;
do|do
block|{
name|fieldName
operator|=
name|randomString
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|fieldName
operator|.
name|contains
argument_list|(
literal|"."
argument_list|)
condition|)
do|;
return|return
name|fieldName
return|;
block|}
comment|/**      * Returns a randomly selected existing field name out of the fields that are contained      * in the document provided as an argument.      */
DECL|method|randomExistingFieldName
specifier|public
specifier|static
name|String
name|randomExistingFieldName
parameter_list|(
name|Random
name|random
parameter_list|,
name|IngestDocument
name|ingestDocument
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|randomEntry
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|source
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|randomEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
while|while
condition|(
name|randomEntry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|randomEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|treeMap
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|randomEntry
operator|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|treeMap
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|+=
literal|"."
operator|+
name|randomEntry
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
assert|assert
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|key
argument_list|,
name|Object
operator|.
name|class
argument_list|)
operator|!=
literal|null
assert|;
return|return
name|key
return|;
block|}
comment|/**      * Adds a random non existing field to the provided document and associates it      * with the provided value. The field will be added at a random position within the document,      * not necessarily at the top level using a leaf field name.      */
DECL|method|addRandomField
specifier|public
specifier|static
name|String
name|addRandomField
parameter_list|(
name|Random
name|random
parameter_list|,
name|IngestDocument
name|ingestDocument
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|String
name|fieldName
decl_stmt|;
do|do
block|{
name|fieldName
operator|=
name|randomFieldName
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|canAddField
argument_list|(
name|fieldName
argument_list|,
name|ingestDocument
argument_list|)
operator|==
literal|false
condition|)
do|;
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|fieldName
return|;
block|}
comment|/**      * Checks whether the provided field name can be safely added to the provided document.      * When the provided field name holds the path using the dot notation, we have to make sure      * that each node of the tree either doesn't exist or is a map, otherwise new fields cannot be added.      */
DECL|method|canAddField
specifier|public
specifier|static
name|boolean
name|canAddField
parameter_list|(
name|String
name|path
parameter_list|,
name|IngestDocument
name|ingestDocument
parameter_list|)
block|{
name|String
index|[]
name|pathElements
init|=
name|path
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|innerMap
init|=
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathElements
operator|.
name|length
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pathElements
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|currentLevel
init|=
name|innerMap
operator|.
name|get
argument_list|(
name|pathElements
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentLevel
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentLevel
operator|instanceof
name|Map
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|currentLevel
decl_stmt|;
name|innerMap
operator|=
name|map
expr_stmt|;
block|}
block|}
name|String
name|leafKey
init|=
name|pathElements
index|[
name|pathElements
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
return|return
name|innerMap
operator|.
name|containsKey
argument_list|(
name|leafKey
argument_list|)
operator|==
literal|false
return|;
block|}
comment|/**      * Generates a random document and random metadata      */
DECL|method|randomIngestDocument
specifier|public
specifier|static
name|IngestDocument
name|randomIngestDocument
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|randomIngestDocument
argument_list|(
name|random
argument_list|,
name|randomSource
argument_list|(
name|random
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Generates a document that holds random metadata and the document provided as a map argument      */
DECL|method|randomIngestDocument
specifier|public
specifier|static
name|IngestDocument
name|randomIngestDocument
parameter_list|(
name|Random
name|random
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|String
name|index
init|=
name|randomString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|randomString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|randomString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|String
name|routing
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|routing
operator|=
name|randomString
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
name|String
name|parent
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|parent
operator|=
name|randomString
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IngestDocument
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|routing
argument_list|,
name|parent
argument_list|,
name|source
argument_list|)
return|;
block|}
DECL|method|randomSource
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|randomSource
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|addRandomFields
argument_list|(
name|random
argument_list|,
name|document
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
comment|/**      * Generates a random field value, can be a string, a number, a list of an object itself.      */
DECL|method|randomFieldValue
specifier|public
specifier|static
name|Object
name|randomFieldValue
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|randomFieldValue
argument_list|(
name|random
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|randomFieldValue
specifier|private
specifier|static
name|Object
name|randomFieldValue
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|currentDepth
parameter_list|)
block|{
switch|switch
condition|(
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
literal|9
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|randomString
argument_list|(
name|random
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|random
operator|.
name|nextInt
argument_list|()
return|;
case|case
literal|2
case|:
return|return
name|random
operator|.
name|nextBoolean
argument_list|()
return|;
case|case
literal|3
case|:
return|return
name|random
operator|.
name|nextDouble
argument_list|()
return|;
case|case
literal|4
case|:
name|List
argument_list|<
name|String
argument_list|>
name|stringList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numStringItems
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numStringItems
condition|;
name|j
operator|++
control|)
block|{
name|stringList
operator|.
name|add
argument_list|(
name|randomString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stringList
return|;
case|case
literal|5
case|:
name|List
argument_list|<
name|Integer
argument_list|>
name|intList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numIntItems
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numIntItems
condition|;
name|j
operator|++
control|)
block|{
name|intList
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|intList
return|;
case|case
literal|6
case|:
name|List
argument_list|<
name|Boolean
argument_list|>
name|booleanList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numBooleanItems
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numBooleanItems
condition|;
name|j
operator|++
control|)
block|{
name|booleanList
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|booleanList
return|;
case|case
literal|7
case|:
name|List
argument_list|<
name|Double
argument_list|>
name|doubleList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numDoubleItems
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDoubleItems
condition|;
name|j
operator|++
control|)
block|{
name|doubleList
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|doubleList
return|;
case|case
literal|8
case|:
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newNode
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|addRandomFields
argument_list|(
name|random
argument_list|,
name|newNode
argument_list|,
operator|++
name|currentDepth
argument_list|)
expr_stmt|;
return|return
name|newNode
return|;
case|case
literal|9
case|:
name|byte
index|[]
name|byteArray
init|=
operator|new
name|byte
index|[
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|1024
argument_list|)
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|byteArray
argument_list|)
expr_stmt|;
return|return
name|byteArray
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|method|randomString
specifier|public
specifier|static
name|String
name|randomString
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
name|RandomStrings
operator|.
name|randomAsciiOfLengthBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
return|;
block|}
return|return
name|RandomStrings
operator|.
name|randomUnicodeOfCodepointLengthBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
return|;
block|}
DECL|method|addRandomFields
specifier|private
specifier|static
name|void
name|addRandomFields
parameter_list|(
name|Random
name|random
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parentNode
parameter_list|,
name|int
name|currentDepth
parameter_list|)
block|{
if|if
condition|(
name|currentDepth
operator|>
literal|5
condition|)
block|{
return|return;
block|}
name|int
name|numFields
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fieldName
init|=
name|randomLeafFieldName
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Object
name|fieldValue
init|=
name|randomFieldValue
argument_list|(
name|random
argument_list|,
name|currentDepth
argument_list|)
decl_stmt|;
name|parentNode
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

