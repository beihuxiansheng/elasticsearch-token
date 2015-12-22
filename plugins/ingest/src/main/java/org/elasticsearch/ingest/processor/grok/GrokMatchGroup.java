begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor.grok
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|grok
package|;
end_package

begin_class
DECL|class|GrokMatchGroup
specifier|final
class|class
name|GrokMatchGroup
block|{
DECL|field|DEFAULT_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TYPE
init|=
literal|"string"
decl_stmt|;
DECL|field|patternName
specifier|private
specifier|final
name|String
name|patternName
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|groupValue
specifier|private
specifier|final
name|String
name|groupValue
decl_stmt|;
DECL|method|GrokMatchGroup
specifier|public
name|GrokMatchGroup
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|groupValue
parameter_list|)
block|{
name|String
index|[]
name|parts
init|=
name|groupName
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|patternName
operator|=
name|parts
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|>=
literal|2
condition|)
block|{
name|fieldName
operator|=
name|parts
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|fieldName
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|type
operator|=
name|parts
index|[
literal|2
index|]
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
block|}
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
operator|(
name|fieldName
operator|==
literal|null
operator|)
condition|?
name|patternName
else|:
name|fieldName
return|;
block|}
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|"int"
case|:
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|groupValue
argument_list|)
return|;
case|case
literal|"float"
case|:
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|groupValue
argument_list|)
return|;
default|default:
return|return
name|groupValue
return|;
block|}
block|}
block|}
end_class

end_unit

