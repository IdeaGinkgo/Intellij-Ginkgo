package test

var _ = Describe("Book", func() {
	DescribeTable("Category Table",
		func(b Book, expected string) {
			Expect(b.CategoryByLength()).To(Equal(expected))
		},
		<caret>Entry("Novel", Book{Pages: 2783}, "NOVEL"),
		Entry("Short Story", Book{Pages: 24}, "SHORT STORY"),
	)
})
