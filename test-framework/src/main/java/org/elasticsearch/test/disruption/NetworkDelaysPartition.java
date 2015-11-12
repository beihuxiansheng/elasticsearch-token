begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.disruption
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|disruption
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
name|test
operator|.
name|transport
operator|.
name|MockTransportService
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
name|Set
import|;
end_import

begin_class
DECL|class|NetworkDelaysPartition
specifier|public
class|class
name|NetworkDelaysPartition
extends|extends
name|NetworkPartition
block|{
DECL|field|DEFAULT_DELAY_MIN
specifier|static
name|long
name|DEFAULT_DELAY_MIN
init|=
literal|10000
decl_stmt|;
DECL|field|DEFAULT_DELAY_MAX
specifier|static
name|long
name|DEFAULT_DELAY_MAX
init|=
literal|90000
decl_stmt|;
DECL|field|delayMin
specifier|final
name|long
name|delayMin
decl_stmt|;
DECL|field|delayMax
specifier|final
name|long
name|delayMax
decl_stmt|;
DECL|field|duration
name|TimeValue
name|duration
decl_stmt|;
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
name|DEFAULT_DELAY_MIN
argument_list|,
name|DEFAULT_DELAY_MAX
argument_list|)
expr_stmt|;
block|}
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|Random
name|random
parameter_list|,
name|long
name|delayMin
parameter_list|,
name|long
name|delayMax
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|delayMin
operator|=
name|delayMin
expr_stmt|;
name|this
operator|.
name|delayMax
operator|=
name|delayMax
expr_stmt|;
block|}
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|String
name|node1
parameter_list|,
name|String
name|node2
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|,
name|DEFAULT_DELAY_MIN
argument_list|,
name|DEFAULT_DELAY_MAX
argument_list|,
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|String
name|node1
parameter_list|,
name|String
name|node2
parameter_list|,
name|long
name|delayMin
parameter_list|,
name|long
name|delayMax
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|delayMin
operator|=
name|delayMin
expr_stmt|;
name|this
operator|.
name|delayMax
operator|=
name|delayMax
expr_stmt|;
block|}
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodesSideOne
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodesSideTwo
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|nodesSideOne
argument_list|,
name|nodesSideTwo
argument_list|,
name|DEFAULT_DELAY_MIN
argument_list|,
name|DEFAULT_DELAY_MAX
argument_list|,
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodesSideOne
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodesSideTwo
parameter_list|,
name|long
name|delay
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|nodesSideOne
argument_list|,
name|nodesSideTwo
argument_list|,
name|delay
argument_list|,
name|delay
argument_list|,
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|NetworkDelaysPartition
specifier|public
name|NetworkDelaysPartition
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodesSideOne
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodesSideTwo
parameter_list|,
name|long
name|delayMin
parameter_list|,
name|long
name|delayMax
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|nodesSideOne
argument_list|,
name|nodesSideTwo
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|delayMin
operator|=
name|delayMin
expr_stmt|;
name|this
operator|.
name|delayMax
operator|=
name|delayMax
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDisrupting
specifier|public
specifier|synchronized
name|void
name|startDisrupting
parameter_list|()
block|{
name|duration
operator|=
operator|new
name|TimeValue
argument_list|(
name|delayMin
operator|==
name|delayMax
condition|?
name|delayMin
else|:
name|delayMin
operator|+
name|random
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|delayMax
operator|-
name|delayMin
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyDisruption
name|void
name|applyDisruption
parameter_list|(
name|MockTransportService
name|transportService1
parameter_list|,
name|MockTransportService
name|transportService2
parameter_list|)
block|{
name|transportService1
operator|.
name|addUnresponsiveRule
argument_list|(
name|transportService1
argument_list|,
name|duration
argument_list|)
expr_stmt|;
name|transportService1
operator|.
name|addUnresponsiveRule
argument_list|(
name|transportService2
argument_list|,
name|duration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPartitionDescription
specifier|protected
name|String
name|getPartitionDescription
parameter_list|()
block|{
return|return
literal|"network delays for ["
operator|+
name|duration
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
DECL|method|expectedTimeToHeal
specifier|public
name|TimeValue
name|expectedTimeToHeal
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|delayMax
argument_list|)
return|;
block|}
block|}
end_class

end_unit

