begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.component
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
package|;
end_package

begin_class
DECL|class|LifecycleListener
specifier|public
specifier|abstract
class|class
name|LifecycleListener
block|{
DECL|method|beforeStart
specifier|public
name|void
name|beforeStart
parameter_list|()
block|{      }
DECL|method|afterStart
specifier|public
name|void
name|afterStart
parameter_list|()
block|{      }
DECL|method|beforeStop
specifier|public
name|void
name|beforeStop
parameter_list|()
block|{      }
DECL|method|afterStop
specifier|public
name|void
name|afterStop
parameter_list|()
block|{      }
DECL|method|beforeClose
specifier|public
name|void
name|beforeClose
parameter_list|()
block|{      }
DECL|method|afterClose
specifier|public
name|void
name|afterClose
parameter_list|()
block|{      }
block|}
end_class

end_unit

