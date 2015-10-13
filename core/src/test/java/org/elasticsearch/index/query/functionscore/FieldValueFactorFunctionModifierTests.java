begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|FieldValueFactorFunction
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|FieldValueFactorFunctionModifierTests
specifier|public
class|class
name|FieldValueFactorFunctionModifierTests
extends|extends
name|ESTestCase
block|{
DECL|method|testValidOrdinals
specifier|public
name|void
name|testValidOrdinals
parameter_list|()
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG1P
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG2P
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN1P
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN2P
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQUARE
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQRT
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
operator|.
name|ordinal
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWriteTo
specifier|public
name|void
name|testWriteTo
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG1P
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG2P
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN1P
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN2P
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQUARE
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQRT
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testReadFrom
specifier|public
name|void
name|testReadFrom
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG1P
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG2P
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|5
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN1P
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|6
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN2P
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|7
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQUARE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|8
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQRT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|9
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readModifierFrom
argument_list|(
name|in
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFromString
specifier|public
name|void
name|testFromString
parameter_list|()
block|{
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"none"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"log"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"log1p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG1P
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"log2p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LOG2P
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"ln"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"ln1p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN1P
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"ln2p"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|LN2P
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"square"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQUARE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"sqrt"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|SQRT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
literal|"reciprocal"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
