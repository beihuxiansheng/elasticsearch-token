begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|StoreUtils
specifier|public
specifier|final
class|class
name|StoreUtils
block|{
DECL|method|StoreUtils
specifier|private
name|StoreUtils
parameter_list|()
block|{      }
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Directory
name|directory
parameter_list|)
block|{
if|if
condition|(
name|directory
operator|instanceof
name|NIOFSDirectory
condition|)
block|{
name|NIOFSDirectory
name|niofsDirectory
init|=
operator|(
name|NIOFSDirectory
operator|)
name|directory
decl_stmt|;
return|return
literal|"niofs("
operator|+
name|niofsDirectory
operator|.
name|getDirectory
argument_list|()
operator|+
literal|")"
return|;
block|}
if|if
condition|(
name|directory
operator|instanceof
name|MMapDirectory
condition|)
block|{
name|MMapDirectory
name|mMapDirectory
init|=
operator|(
name|MMapDirectory
operator|)
name|directory
decl_stmt|;
return|return
literal|"mmapfs("
operator|+
name|mMapDirectory
operator|.
name|getDirectory
argument_list|()
operator|+
literal|")"
return|;
block|}
if|if
condition|(
name|directory
operator|instanceof
name|SimpleFSDirectory
condition|)
block|{
name|SimpleFSDirectory
name|simpleFSDirectory
init|=
operator|(
name|SimpleFSDirectory
operator|)
name|directory
decl_stmt|;
return|return
literal|"simplefs("
operator|+
name|simpleFSDirectory
operator|.
name|getDirectory
argument_list|()
operator|+
literal|")"
return|;
block|}
return|return
name|directory
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

