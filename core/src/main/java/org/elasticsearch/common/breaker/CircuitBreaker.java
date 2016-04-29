begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|breaker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Interface for an object that can be incremented, breaking after some  * configured limit has been reached.  */
end_comment

begin_interface
DECL|interface|CircuitBreaker
specifier|public
interface|interface
name|CircuitBreaker
block|{
DECL|field|PARENT
name|String
name|PARENT
init|=
literal|"parent"
decl_stmt|;
DECL|field|FIELDDATA
name|String
name|FIELDDATA
init|=
literal|"fielddata"
decl_stmt|;
DECL|field|REQUEST
name|String
name|REQUEST
init|=
literal|"request"
decl_stmt|;
DECL|field|IN_FLIGHT_REQUESTS
name|String
name|IN_FLIGHT_REQUESTS
init|=
literal|"in_flight_requests"
decl_stmt|;
DECL|enum|Type
enum|enum
name|Type
block|{
comment|// A regular or child MemoryCircuitBreaker
DECL|enum constant|MEMORY
name|MEMORY
block|,
comment|// A special parent-type for the hierarchy breaker service
DECL|enum constant|PARENT
name|PARENT
block|,
comment|// A breaker where every action is a noop, it never breaks
DECL|enum constant|NOOP
name|NOOP
block|;
DECL|method|parseValue
specifier|public
specifier|static
name|Type
name|parseValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
condition|)
block|{
case|case
literal|"noop"
case|:
return|return
name|Type
operator|.
name|NOOP
return|;
case|case
literal|"parent"
case|:
return|return
name|Type
operator|.
name|PARENT
return|;
case|case
literal|"memory"
case|:
return|return
name|Type
operator|.
name|MEMORY
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No CircuitBreaker with type: "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Trip the circuit breaker      * @param fieldName name of the field responsible for tripping the breaker      * @param bytesNeeded bytes asked for but unable to be allocated      */
DECL|method|circuitBreak
name|void
name|circuitBreak
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|long
name|bytesNeeded
parameter_list|)
function_decl|;
comment|/**      * add bytes to the breaker and maybe trip      * @param bytes number of bytes to add      * @param label string label describing the bytes being added      * @return the number of "used" bytes for the circuit breaker      */
DECL|method|addEstimateBytesAndMaybeBreak
name|double
name|addEstimateBytesAndMaybeBreak
parameter_list|(
name|long
name|bytes
parameter_list|,
name|String
name|label
parameter_list|)
throws|throws
name|CircuitBreakingException
function_decl|;
comment|/**      * Adjust the circuit breaker without tripping      */
DECL|method|addWithoutBreaking
name|long
name|addWithoutBreaking
parameter_list|(
name|long
name|bytes
parameter_list|)
function_decl|;
comment|/**      * @return the currently used bytes the breaker is tracking      */
DECL|method|getUsed
name|long
name|getUsed
parameter_list|()
function_decl|;
comment|/**      * @return maximum number of bytes the circuit breaker can track before tripping      */
DECL|method|getLimit
name|long
name|getLimit
parameter_list|()
function_decl|;
comment|/**      * @return overhead of circuit breaker      */
DECL|method|getOverhead
name|double
name|getOverhead
parameter_list|()
function_decl|;
comment|/**      * @return the number of times the circuit breaker has been tripped      */
DECL|method|getTrippedCount
name|long
name|getTrippedCount
parameter_list|()
function_decl|;
comment|/**      * @return the name of the breaker      */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

