begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RecoveryStatus
specifier|public
class|class
name|RecoveryStatus
block|{
DECL|enum|Stage
specifier|public
specifier|static
enum|enum
name|Stage
block|{
DECL|enum constant|INIT
name|INIT
block|,
DECL|enum constant|INDEX
name|INDEX
block|,
DECL|enum constant|START
name|START
block|,
DECL|enum constant|TRANSLOG
name|TRANSLOG
block|,
DECL|enum constant|DONE
name|DONE
block|}
DECL|field|stage
specifier|private
name|Stage
name|stage
init|=
name|Stage
operator|.
name|INIT
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|time
specifier|private
name|long
name|time
decl_stmt|;
DECL|field|index
specifier|private
name|Index
name|index
init|=
operator|new
name|Index
argument_list|()
decl_stmt|;
DECL|field|translog
specifier|private
name|Translog
name|translog
init|=
operator|new
name|Translog
argument_list|()
decl_stmt|;
DECL|field|start
specifier|private
name|Start
name|start
init|=
operator|new
name|Start
argument_list|()
decl_stmt|;
DECL|method|stage
specifier|public
name|Stage
name|stage
parameter_list|()
block|{
return|return
name|this
operator|.
name|stage
return|;
block|}
DECL|method|updateStage
specifier|public
name|RecoveryStatus
name|updateStage
parameter_list|(
name|Stage
name|stage
parameter_list|)
block|{
name|this
operator|.
name|stage
operator|=
name|stage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|startTime
specifier|public
name|void
name|startTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
DECL|method|time
specifier|public
name|long
name|time
parameter_list|()
block|{
return|return
name|this
operator|.
name|time
return|;
block|}
DECL|method|time
specifier|public
name|void
name|time
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|start
specifier|public
name|Start
name|start
parameter_list|()
block|{
return|return
name|this
operator|.
name|start
return|;
block|}
DECL|method|translog
specifier|public
name|Translog
name|translog
parameter_list|()
block|{
return|return
name|translog
return|;
block|}
DECL|class|Start
specifier|public
specifier|static
class|class
name|Start
block|{
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|time
specifier|private
name|long
name|time
decl_stmt|;
DECL|field|checkIndexTime
specifier|private
name|long
name|checkIndexTime
decl_stmt|;
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|startTime
specifier|public
name|void
name|startTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
DECL|method|time
specifier|public
name|long
name|time
parameter_list|()
block|{
return|return
name|this
operator|.
name|time
return|;
block|}
DECL|method|time
specifier|public
name|void
name|time
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
DECL|method|checkIndexTime
specifier|public
name|long
name|checkIndexTime
parameter_list|()
block|{
return|return
name|checkIndexTime
return|;
block|}
DECL|method|checkIndexTime
specifier|public
name|void
name|checkIndexTime
parameter_list|(
name|long
name|checkIndexTime
parameter_list|)
block|{
name|this
operator|.
name|checkIndexTime
operator|=
name|checkIndexTime
expr_stmt|;
block|}
block|}
DECL|class|Translog
specifier|public
specifier|static
class|class
name|Translog
block|{
DECL|field|startTime
specifier|private
name|long
name|startTime
init|=
literal|0
decl_stmt|;
DECL|field|time
specifier|private
name|long
name|time
decl_stmt|;
DECL|field|currentTranslogOperations
specifier|private
specifier|volatile
name|int
name|currentTranslogOperations
init|=
literal|0
decl_stmt|;
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|startTime
specifier|public
name|void
name|startTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
DECL|method|time
specifier|public
name|long
name|time
parameter_list|()
block|{
return|return
name|this
operator|.
name|time
return|;
block|}
DECL|method|time
specifier|public
name|void
name|time
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
DECL|method|addTranslogOperations
specifier|public
name|void
name|addTranslogOperations
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|currentTranslogOperations
operator|+=
name|count
expr_stmt|;
block|}
DECL|method|currentTranslogOperations
specifier|public
name|int
name|currentTranslogOperations
parameter_list|()
block|{
return|return
name|this
operator|.
name|currentTranslogOperations
return|;
block|}
block|}
DECL|class|Index
specifier|public
specifier|static
class|class
name|Index
block|{
DECL|field|startTime
specifier|private
name|long
name|startTime
init|=
literal|0
decl_stmt|;
DECL|field|time
specifier|private
name|long
name|time
init|=
literal|0
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numberOfFiles
specifier|private
name|int
name|numberOfFiles
init|=
literal|0
decl_stmt|;
DECL|field|totalSize
specifier|private
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
DECL|field|numberOfReusedFiles
specifier|private
name|int
name|numberOfReusedFiles
init|=
literal|0
decl_stmt|;
DECL|field|reusedTotalSize
specifier|private
name|long
name|reusedTotalSize
init|=
literal|0
decl_stmt|;
DECL|field|currentFilesSize
specifier|private
name|AtomicLong
name|currentFilesSize
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|startTime
specifier|public
name|void
name|startTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
DECL|method|time
specifier|public
name|long
name|time
parameter_list|()
block|{
return|return
name|this
operator|.
name|time
return|;
block|}
DECL|method|time
specifier|public
name|void
name|time
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|int
name|numberOfFiles
parameter_list|,
name|long
name|totalSize
parameter_list|,
name|int
name|numberOfReusedFiles
parameter_list|,
name|long
name|reusedTotalSize
parameter_list|)
block|{
name|this
operator|.
name|numberOfFiles
operator|=
name|numberOfFiles
expr_stmt|;
name|this
operator|.
name|totalSize
operator|=
name|totalSize
expr_stmt|;
name|this
operator|.
name|numberOfReusedFiles
operator|=
name|numberOfReusedFiles
expr_stmt|;
name|this
operator|.
name|reusedTotalSize
operator|=
name|reusedTotalSize
expr_stmt|;
block|}
DECL|method|numberOfFiles
specifier|public
name|int
name|numberOfFiles
parameter_list|()
block|{
return|return
name|numberOfFiles
return|;
block|}
DECL|method|numberOfRecoveredFiles
specifier|public
name|int
name|numberOfRecoveredFiles
parameter_list|()
block|{
return|return
name|numberOfFiles
operator|-
name|numberOfReusedFiles
return|;
block|}
DECL|method|totalSize
specifier|public
name|long
name|totalSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalSize
return|;
block|}
DECL|method|numberOfReusedFiles
specifier|public
name|int
name|numberOfReusedFiles
parameter_list|()
block|{
return|return
name|numberOfReusedFiles
return|;
block|}
DECL|method|reusedTotalSize
specifier|public
name|long
name|reusedTotalSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|reusedTotalSize
return|;
block|}
DECL|method|recoveredTotalSize
specifier|public
name|long
name|recoveredTotalSize
parameter_list|()
block|{
return|return
name|totalSize
operator|-
name|reusedTotalSize
return|;
block|}
DECL|method|updateVersion
specifier|public
name|void
name|updateVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|currentFilesSize
specifier|public
name|long
name|currentFilesSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|currentFilesSize
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addCurrentFilesSize
specifier|public
name|void
name|addCurrentFilesSize
parameter_list|(
name|long
name|updatedSize
parameter_list|)
block|{
name|this
operator|.
name|currentFilesSize
operator|.
name|addAndGet
argument_list|(
name|updatedSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

