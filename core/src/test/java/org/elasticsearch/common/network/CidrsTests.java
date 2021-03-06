begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
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
name|collect
operator|.
name|Tuple
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
name|ESTestCase
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasToString
import|;
end_import

begin_class
DECL|class|CidrsTests
specifier|public
class|class
name|CidrsTests
extends|extends
name|ESTestCase
block|{
DECL|method|testNullCidr
specifier|public
name|void
name|testNullCidr
parameter_list|()
block|{
try|try
block|{
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected NullPointerException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"cidr"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSplittingSlash
specifier|public
name|void
name|testSplittingSlash
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1.2.3.4/32/32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1.2.3.4/"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|test
range|:
name|cases
control|)
block|{
try|try
block|{
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalArgumentException after splitting"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"expected [a.b.c.d, e]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"splitting on \"/\""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSplittingDot
specifier|public
name|void
name|testSplittingDot
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1.2.3/32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1/32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1./32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1../32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1.../32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"1.2.3.4.5/32"
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"/32"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|test
range|:
name|cases
control|)
block|{
try|try
block|{
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalArgumentException after splitting"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"unable to parse"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"as an IP address literal"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testValidSpecificCases
specifier|public
name|void
name|testValidSpecificCases
parameter_list|()
block|{
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|long
index|[]
argument_list|>
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"192.168.0.0/24"
argument_list|,
operator|new
name|long
index|[]
block|{
operator|(
literal|192L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|168
operator|<<
literal|16
operator|)
block|,
operator|(
literal|192L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|168
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|1
operator|<<
literal|8
operator|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"192.168.128.0/17"
argument_list|,
operator|new
name|long
index|[]
block|{
operator|(
literal|192L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|168
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|128
operator|<<
literal|8
operator|)
block|,
operator|(
literal|192L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|168
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|128
operator|<<
literal|8
operator|)
operator|+
operator|(
literal|1
operator|<<
literal|15
operator|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"128.0.0.0/1"
argument_list|,
operator|new
name|long
index|[]
block|{
literal|128L
operator|<<
literal|24
block|,
operator|(
literal|128L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|1L
operator|<<
literal|31
operator|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// edge case
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"0.0.0.0/0"
argument_list|,
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|1L
operator|<<
literal|32
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// edge case
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"0.0.0.0/1"
argument_list|,
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|1L
operator|<<
literal|31
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// edge case
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
literal|"192.168.1.1/32"
argument_list|,
operator|new
name|long
index|[]
block|{
operator|(
literal|192L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|168L
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|1L
operator|<<
literal|8
operator|)
operator|+
literal|1L
block|,
operator|(
literal|192L
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|168L
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|1L
operator|<<
literal|8
operator|)
operator|+
literal|1L
operator|+
literal|1
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// edge case
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|long
index|[]
argument_list|>
name|test
range|:
name|cases
control|)
block|{
name|long
index|[]
name|actual
init|=
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|test
operator|.
name|v1
argument_list|()
argument_list|,
name|test
operator|.
name|v2
argument_list|()
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidSpecificOctetCases
specifier|public
name|void
name|testInvalidSpecificOctetCases
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"256.0.0.0/8"
argument_list|)
expr_stmt|;
comment|// first octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"255.256.0.0/16"
argument_list|)
expr_stmt|;
comment|// second octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"255.255.256.0/24"
argument_list|)
expr_stmt|;
comment|// third octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"255.255.255.256/32"
argument_list|)
expr_stmt|;
comment|// fourth octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"abc.0.0.0/8"
argument_list|)
expr_stmt|;
comment|// octet that can not be parsed
name|cases
operator|.
name|add
argument_list|(
literal|"-1.0.0.0/8"
argument_list|)
expr_stmt|;
comment|// first octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"128.-1.0.0/16"
argument_list|)
expr_stmt|;
comment|// second octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"128.128.-1.0/24"
argument_list|)
expr_stmt|;
comment|// third octet out of range
name|cases
operator|.
name|add
argument_list|(
literal|"128.128.128.-1/32"
argument_list|)
expr_stmt|;
comment|// fourth octet out of range
for|for
control|(
name|String
name|test
range|:
name|cases
control|)
block|{
try|try
block|{
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected invalid address"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"unable to parse"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"as an IP address literal"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testInvalidSpecificNetworkMaskCases
specifier|public
name|void
name|testInvalidSpecificNetworkMaskCases
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"128.128.128.128/-1"
argument_list|)
expr_stmt|;
comment|// network mask out of range
name|cases
operator|.
name|add
argument_list|(
literal|"128.128.128.128/33"
argument_list|)
expr_stmt|;
comment|// network mask out of range
name|cases
operator|.
name|add
argument_list|(
literal|"128.128.128.128/abc"
argument_list|)
expr_stmt|;
comment|// network mask that can not be parsed
for|for
control|(
name|String
name|test
range|:
name|cases
control|)
block|{
try|try
block|{
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected invalid network mask"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"network mask"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testValidCombinations
specifier|public
name|void
name|testValidCombinations
parameter_list|()
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
literal|1
operator|<<
literal|16
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|String
name|octetsString
init|=
name|Cidrs
operator|.
name|octetsToString
argument_list|(
name|Cidrs
operator|.
name|longToOctets
argument_list|(
name|i
operator|<<
literal|16
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|mask
init|=
literal|16
init|;
name|mask
operator|<=
literal|32
condition|;
name|mask
operator|++
control|)
block|{
name|String
name|test
init|=
name|octetsString
operator|+
literal|"/"
operator|+
name|mask
decl_stmt|;
name|long
index|[]
name|actual
init|=
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test
argument_list|,
literal|2
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test
argument_list|,
name|i
operator|<<
literal|16
argument_list|,
name|actual
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test
argument_list|,
operator|(
name|i
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|1L
operator|<<
operator|(
literal|32
operator|-
name|mask
operator|)
operator|)
argument_list|,
name|actual
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testInvalidCombinations
specifier|public
name|void
name|testInvalidCombinations
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
literal|"192.168.0.1/24"
argument_list|)
expr_stmt|;
comment|// invalid because fourth octet is not zero
name|cases
operator|.
name|add
argument_list|(
literal|"192.168.1.0/16"
argument_list|)
expr_stmt|;
comment|// invalid because third octet is not zero
name|cases
operator|.
name|add
argument_list|(
literal|"192.1.0.0/8"
argument_list|)
expr_stmt|;
comment|// invalid because second octet is not zero
name|cases
operator|.
name|add
argument_list|(
literal|"128.0.0.0/0"
argument_list|)
expr_stmt|;
comment|// invalid because first octet is not zero
comment|// create cases that have a bit set outside of the network mask
name|int
name|value
init|=
literal|1
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
literal|31
condition|;
name|i
operator|++
control|)
block|{
name|cases
operator|.
name|add
argument_list|(
name|Cidrs
operator|.
name|octetsToCIDR
argument_list|(
name|Cidrs
operator|.
name|longToOctets
argument_list|(
name|value
argument_list|)
argument_list|,
literal|32
operator|-
name|i
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|<<=
literal|1
expr_stmt|;
block|}
for|for
control|(
name|String
name|test
range|:
name|cases
control|)
block|{
try|try
block|{
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected invalid combination"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|test
argument_list|,
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"invalid address/network mask combination"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRandomValidCombinations
specifier|public
name|void
name|testRandomValidCombinations
parameter_list|()
block|{
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// random number of strings with valid octets and valid network masks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1024
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|int
name|networkMask
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|32
argument_list|)
decl_stmt|;
name|long
name|mask
init|=
operator|(
literal|1L
operator|<<
operator|(
literal|32
operator|-
name|networkMask
operator|)
operator|)
operator|-
literal|1
decl_stmt|;
name|long
name|address
init|=
name|randomLongInIPv4Range
argument_list|()
operator|&
operator|~
name|mask
decl_stmt|;
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|Cidrs
operator|.
name|octetsToCIDR
argument_list|(
name|Cidrs
operator|.
name|longToOctets
argument_list|(
name|address
argument_list|)
argument_list|,
name|networkMask
argument_list|)
argument_list|,
name|networkMask
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|test
range|:
name|cases
control|)
block|{
name|long
index|[]
name|actual
init|=
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|test
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test
operator|.
name|v1
argument_list|()
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test
operator|.
name|v1
argument_list|()
argument_list|,
literal|2
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// assert the resulting block has the right size
name|assertEquals
argument_list|(
name|test
operator|.
name|v1
argument_list|()
argument_list|,
literal|1L
operator|<<
operator|(
literal|32
operator|-
name|test
operator|.
name|v2
argument_list|()
operator|)
argument_list|,
name|actual
index|[
literal|1
index|]
operator|-
name|actual
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomLongInIPv4Range
specifier|private
name|long
name|randomLongInIPv4Range
parameter_list|()
block|{
return|return
name|randomLong
argument_list|()
operator|&
literal|0x00000000FFFFFFFFL
return|;
block|}
block|}
end_class

end_unit

