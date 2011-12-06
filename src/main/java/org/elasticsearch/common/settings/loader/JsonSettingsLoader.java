begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings.loader
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|loader
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
name|xcontent
operator|.
name|XContentFactory
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
name|XContentType
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_comment
comment|/**  * Settings loader that loads (parses) the settings in a json format by flattening them  * into a map.  *  *  */
end_comment

begin_class
DECL|class|JsonSettingsLoader
specifier|public
class|class
name|JsonSettingsLoader
implements|implements
name|SettingsLoader
block|{
annotation|@
name|Override
DECL|method|load
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|load
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|load
argument_list|(
name|parser
argument_list|)
return|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|load
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|load
argument_list|(
name|parser
argument_list|)
return|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|load
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|load
parameter_list|(
name|XContentParser
name|jp
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|settings
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|serializeObject
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|jp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|settings
return|;
block|}
DECL|method|serializeObject
specifier|private
name|void
name|serializeObject
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|settings
parameter_list|,
name|StringBuilder
name|sb
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|String
name|objFieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|objFieldName
operator|!=
literal|null
condition|)
block|{
name|path
operator|.
name|add
argument_list|(
name|objFieldName
argument_list|)
expr_stmt|;
block|}
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|serializeObject
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|parser
argument_list|,
name|currentFieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|serializeArray
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|parser
argument_list|,
name|currentFieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
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
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
comment|// ignore this
block|}
else|else
block|{
name|serializeValue
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|parser
argument_list|,
name|currentFieldName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|objFieldName
operator|!=
literal|null
condition|)
block|{
name|path
operator|.
name|remove
argument_list|(
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|serializeArray
specifier|private
name|void
name|serializeArray
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|settings
parameter_list|,
name|StringBuilder
name|sb
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|serializeObject
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|parser
argument_list|,
name|fieldName
operator|+
literal|'.'
operator|+
operator|(
name|counter
operator|++
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|serializeArray
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|parser
argument_list|,
name|fieldName
operator|+
literal|'.'
operator|+
operator|(
name|counter
operator|++
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
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
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
comment|// ignore
block|}
else|else
block|{
name|serializeValue
argument_list|(
name|settings
argument_list|,
name|sb
argument_list|,
name|path
argument_list|,
name|parser
argument_list|,
name|fieldName
operator|+
literal|'.'
operator|+
operator|(
name|counter
operator|++
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|serializeValue
specifier|private
name|void
name|serializeValue
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|settings
parameter_list|,
name|StringBuilder
name|sb
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|pathEle
range|:
name|path
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|pathEle
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|settings
operator|.
name|put
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

