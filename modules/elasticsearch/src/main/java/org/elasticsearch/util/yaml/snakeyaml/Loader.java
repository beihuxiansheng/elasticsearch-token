begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|composer
operator|.
name|Composer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|constructor
operator|.
name|BaseConstructor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|constructor
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|error
operator|.
name|YAMLException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|events
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|nodes
operator|.
name|YamlNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|parser
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|parser
operator|.
name|ParserImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|reader
operator|.
name|StreamReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|resolver
operator|.
name|Resolver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * @see<a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information  */
end_comment

begin_class
DECL|class|Loader
specifier|public
class|class
name|Loader
block|{
DECL|field|constructor
specifier|protected
specifier|final
name|BaseConstructor
name|constructor
decl_stmt|;
DECL|field|resolver
specifier|protected
name|Resolver
name|resolver
decl_stmt|;
DECL|field|attached
specifier|private
name|boolean
name|attached
init|=
literal|false
decl_stmt|;
DECL|method|Loader
specifier|public
name|Loader
parameter_list|(
name|BaseConstructor
name|constructor
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|constructor
operator|=
name|constructor
expr_stmt|;
block|}
DECL|method|Loader
specifier|public
name|Loader
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Constructor
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|load
specifier|public
name|Object
name|load
parameter_list|(
name|Reader
name|io
parameter_list|)
block|{
name|Composer
name|composer
init|=
operator|new
name|Composer
argument_list|(
operator|new
name|ParserImpl
argument_list|(
operator|new
name|StreamReader
argument_list|(
name|io
argument_list|)
argument_list|)
argument_list|,
name|resolver
argument_list|)
decl_stmt|;
name|constructor
operator|.
name|setComposer
argument_list|(
name|composer
argument_list|)
expr_stmt|;
return|return
name|constructor
operator|.
name|getSingleData
argument_list|()
return|;
block|}
DECL|method|loadAll
specifier|public
name|Iterable
argument_list|<
name|Object
argument_list|>
name|loadAll
parameter_list|(
name|Reader
name|yaml
parameter_list|)
block|{
name|Composer
name|composer
init|=
operator|new
name|Composer
argument_list|(
operator|new
name|ParserImpl
argument_list|(
operator|new
name|StreamReader
argument_list|(
name|yaml
argument_list|)
argument_list|)
argument_list|,
name|resolver
argument_list|)
decl_stmt|;
name|this
operator|.
name|constructor
operator|.
name|setComposer
argument_list|(
name|composer
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|Iterator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|constructor
operator|.
name|checkData
argument_list|()
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
return|return
name|constructor
operator|.
name|getData
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|YamlIterable
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**      * Parse the first YAML document in a stream and produce the corresponding      * representation tree.      *      * @param yaml YAML document      * @return parsed root Node for the specified YAML document      */
DECL|method|compose
specifier|public
name|YamlNode
name|compose
parameter_list|(
name|Reader
name|yaml
parameter_list|)
block|{
name|Composer
name|composer
init|=
operator|new
name|Composer
argument_list|(
operator|new
name|ParserImpl
argument_list|(
operator|new
name|StreamReader
argument_list|(
name|yaml
argument_list|)
argument_list|)
argument_list|,
name|resolver
argument_list|)
decl_stmt|;
name|this
operator|.
name|constructor
operator|.
name|setComposer
argument_list|(
name|composer
argument_list|)
expr_stmt|;
return|return
name|composer
operator|.
name|getSingleNode
argument_list|()
return|;
block|}
comment|/**      * Parse all YAML documents in a stream and produce corresponding      * representation trees.      *      * @param yaml stream of YAML documents      * @return parsed root Nodes for all the specified YAML documents      */
DECL|method|composeAll
specifier|public
name|Iterable
argument_list|<
name|YamlNode
argument_list|>
name|composeAll
parameter_list|(
name|Reader
name|yaml
parameter_list|)
block|{
specifier|final
name|Composer
name|composer
init|=
operator|new
name|Composer
argument_list|(
operator|new
name|ParserImpl
argument_list|(
operator|new
name|StreamReader
argument_list|(
name|yaml
argument_list|)
argument_list|)
argument_list|,
name|resolver
argument_list|)
decl_stmt|;
name|this
operator|.
name|constructor
operator|.
name|setComposer
argument_list|(
name|composer
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|YamlNode
argument_list|>
name|result
init|=
operator|new
name|Iterator
argument_list|<
name|YamlNode
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|composer
operator|.
name|checkNode
argument_list|()
return|;
block|}
specifier|public
name|YamlNode
name|next
parameter_list|()
block|{
return|return
name|composer
operator|.
name|getNode
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|NodeIterable
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|class|NodeIterable
specifier|private
class|class
name|NodeIterable
implements|implements
name|Iterable
argument_list|<
name|YamlNode
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|YamlNode
argument_list|>
name|iterator
decl_stmt|;
DECL|method|NodeIterable
specifier|public
name|NodeIterable
parameter_list|(
name|Iterator
argument_list|<
name|YamlNode
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|YamlNode
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterator
return|;
block|}
block|}
DECL|class|YamlIterable
specifier|private
class|class
name|YamlIterable
implements|implements
name|Iterable
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
decl_stmt|;
DECL|method|YamlIterable
specifier|public
name|YamlIterable
parameter_list|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterator
return|;
block|}
block|}
DECL|method|setResolver
specifier|public
name|void
name|setResolver
parameter_list|(
name|Resolver
name|resolver
parameter_list|)
block|{
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
block|}
comment|/**      * Because Loader is stateful it cannot be shared      */
DECL|method|setAttached
name|void
name|setAttached
parameter_list|()
block|{
if|if
condition|(
operator|!
name|attached
condition|)
block|{
name|attached
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|YAMLException
argument_list|(
literal|"Loader cannot be shared."
argument_list|)
throw|;
block|}
block|}
comment|/**      * Parse a YAML stream and produce parsing events.      *      * @param yaml YAML document(s)      * @return parsed events      */
DECL|method|parse
specifier|public
name|Iterable
argument_list|<
name|Event
argument_list|>
name|parse
parameter_list|(
name|Reader
name|yaml
parameter_list|)
block|{
specifier|final
name|Parser
name|parser
init|=
operator|new
name|ParserImpl
argument_list|(
operator|new
name|StreamReader
argument_list|(
name|yaml
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Event
argument_list|>
name|result
init|=
operator|new
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|parser
operator|.
name|peekEvent
argument_list|()
operator|!=
literal|null
return|;
block|}
specifier|public
name|Event
name|next
parameter_list|()
block|{
return|return
name|parser
operator|.
name|getEvent
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|EventIterable
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|class|EventIterable
specifier|private
class|class
name|EventIterable
implements|implements
name|Iterable
argument_list|<
name|Event
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|Event
argument_list|>
name|iterator
decl_stmt|;
DECL|method|EventIterable
specifier|public
name|EventIterable
parameter_list|(
name|Iterator
argument_list|<
name|Event
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Event
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterator
return|;
block|}
block|}
block|}
end_class

end_unit

