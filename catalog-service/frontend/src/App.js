import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import { Link } from 'react-router-dom';

class App extends Component {
    state = {
        books: []
    }

    async componentDidMount() {
        const response = await fetch('/books');
        const body = await response.json();
        this.setState({ books: body });
    }

    render() {
        const { books, isLoading } = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const bookList = books.map(book => {

            const authorList = book.authors.map(author => {
                return <tr key={author.id}>
                    <td style={{ whiteSpace: 'nowrap' }}>{author.name}</td>
                </tr>

            })

            return <tr key={book.id}>
                <td style={{ whiteSpace: 'nowrap' }}>{book.title}</td>
                <td>{book.isbn}</td>
                <td>{authorList}</td>
            </tr>
        });



        return (
            <div>
                <Container fluid>
                    <h3>Books</h3>
                    <Table className="mt-4">
                        <thead>
                            <tr>
                                <th width="30%">Title</th>
                                <th width="30%">ISBN</th>
                                <th width="30%">Authors</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bookList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default App;
