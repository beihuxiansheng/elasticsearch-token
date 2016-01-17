begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_comment
comment|/**  * An ActionListener that does nothing. Used when we need a listener but don't  * care to listen for the result.  */
end_comment

begin_class
DECL|class|NoopActionListener
specifier|public
specifier|final
class|class
name|NoopActionListener
parameter_list|<
name|Response
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|Response
argument_list|>
block|{
comment|/**      * Get the instance of NoopActionListener cast appropriately.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// Safe because we do nothing with the type.
DECL|method|instance
specifier|public
specifier|static
parameter_list|<
name|Response
parameter_list|>
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|instance
parameter_list|()
block|{
return|return
operator|(
name|ActionListener
argument_list|<
name|Response
argument_list|>
operator|)
name|INSTANCE
return|;
block|}
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|NoopActionListener
argument_list|<
name|Object
argument_list|>
name|INSTANCE
init|=
operator|new
name|NoopActionListener
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|NoopActionListener
specifier|private
name|NoopActionListener
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{     }
block|}
end_class

end_unit

